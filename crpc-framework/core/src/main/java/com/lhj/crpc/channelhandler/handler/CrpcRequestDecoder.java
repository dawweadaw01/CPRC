package com.lhj.crpc.channelhandler.handler;

import com.lhj.crpc.compress.Compressor;
import com.lhj.crpc.compress.CompressorFactory;
import com.lhj.crpc.enumeration.RequestType;
import com.lhj.crpc.message.CrpcRequest;
import com.lhj.crpc.message.MessageFormatConstant;
import com.lhj.crpc.message.RequestPayload;
import com.lhj.crpc.serialize.Serializer;
import com.lhj.crpc.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * * 自定义协议编码器
 * * <p>
 * * <pre>
 *  *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22
 *  *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *  *   |    magic          |ver |head  len|    full length    | qt | ser|comp|              RequestId                |
 *  *   +-----+-----+-------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+---+
 *  *   |                                                                                                             |
 *  *   |                                         body                                                                |
 *  *   |                                                                                                             |
 *  *   +--------------------------------------------------------------------------------------------------------+---+
 *  * </pre>
 * *
 * * 4B magic(魔数)   --->yrpc.getBytes()
 * * 1B version(版本)   ----> 1
 * * 2B header length 首部的长度
 * * 4B full length 报文总长度
 * * 1B serialize
 * * 1B compress
 * * 1B requestType
 * * 8B requestId
 * *
 * * body
 * *
 * * 出站时，第一个经过的处理器
 * * @author banyan
 * * @createTime 2023-07-02
 * <p>
 * 基于长度字段的帧解码器
 *
 * @author banyan
 * @createTime 2023-07-03
 */
@Slf4j
public class CrpcRequestDecoder extends LengthFieldBasedFrameDecoder {
    public CrpcRequestDecoder() {
        super(
            // 找到当前报文的总长度，截取报文，截取出来的报文我们可以去进行解析
            // 最大帧的长度，超过这个maxFrameLength值会直接丢弃
            MessageFormatConstant.MAX_FRAME_LENGTH,
            // 长度的字段的偏移量，
            MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH,
            // 长度的字段的长度
            MessageFormatConstant.FULL_FIELD_LENGTH,
            //  负载的适配长度
            -(MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH
                + MessageFormatConstant.HEADER_FIELD_LENGTH + MessageFormatConstant.FULL_FIELD_LENGTH),
            0);
    }
    
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        
        Thread.sleep(new Random().nextInt(50));
        
        Object decode = super.decode(ctx, in);
        if(decode instanceof ByteBuf byteBuf){
            return decodeFrame(byteBuf);
        }
        return null;
    }
    
    private Object decodeFrame(ByteBuf byteBuf) {
        // 1、解析魔数
        byte[] magic = new byte[MessageFormatConstant.MAGIC.length];
        byteBuf.readBytes(magic);
        // 检测魔数是否匹配
        for (int i = 0; i < magic.length; i++) {
            if(magic[i] != MessageFormatConstant.MAGIC[i]){
                throw new RuntimeException("The request obtained is not legitimate。");
            }
        }
        
        // 2、解析版本号
        byte version = byteBuf.readByte();
        if(version > MessageFormatConstant.VERSION){
            throw new RuntimeException("获得的请求版本不被支持。");
        }
        
        // 3、解析头部的长度
        short headLength = byteBuf.readShort();
        
        // 4、解析总长度
        int fullLength = byteBuf.readInt();
        
        // 5、请求类型
        byte requestType = byteBuf.readByte();
    
        // 6、序列化类型
        byte serializeType = byteBuf.readByte();
        
        // 7、压缩类型
        byte compressType = byteBuf.readByte();
        
        // 8、请求id
        long requestId = byteBuf.readLong();
    
        // 9、时间戳
        long timeStamp = byteBuf.readLong();
    
        // 我们需要封装
        CrpcRequest  crpcRequest = new CrpcRequest();
         crpcRequest.setRequestType(requestType);
         crpcRequest.setCompressType(compressType);
         crpcRequest.setSerializeType(serializeType);
         crpcRequest.setRequestId(requestId);
         crpcRequest.setTimeStamp(timeStamp);
        
        // 心跳请求没有负载，此处可以判断并直接返回
        if( requestType == RequestType.HEART_BEAT.getId()){
            return  crpcRequest;
        }
        
        int payloadLength = fullLength - headLength;
        byte[] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);
        
        // 有了字节数组之后就可以解压缩，反序列化
        // 1、解压缩
        if(payload.length != 0) {
            Compressor compressor = CompressorFactory.getCompressor(compressType).getImpl();
            payload = compressor.decompress(payload);
    
    
            // 2、反序列化
            Serializer serializer = SerializerFactory.getSerializer(serializeType).getImpl();
            RequestPayload requestPayload = serializer.deserialize(payload, RequestPayload.class);
             crpcRequest.setRequestPayload(requestPayload);
        }
        
        if(log.isDebugEnabled()){
            log.debug("请求【{}】已经在服务端完成解码工作。", crpcRequest.getRequestId());
        }
        
        return  crpcRequest;
    }
}
