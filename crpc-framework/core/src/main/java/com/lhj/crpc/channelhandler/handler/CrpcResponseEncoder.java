package com.lhj.crpc.channelhandler.handler;


import com.lhj.crpc.compress.Compressor;
import com.lhj.crpc.compress.CompressorFactory;
import com.lhj.crpc.message.CrpcResponse;
import com.lhj.crpc.message.MessageFormatConstant;
import com.lhj.crpc.serialize.Serializer;
import com.lhj.crpc.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义协议编码器
 * <p>
 * <pre>
 *   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22
 *   +----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+
 *   |    magic          |ver |head  len|    full length    |code  ser|comp|              RequestId                |
 *   +-----+-----+-------+----+----+----+----+-----------+----- ---+--------+----+----+----+----+----+----+---+---+
 *   |                                                                                                             |
 *   |                                         body                                                                |
 *   |                                                                                                             |
 *   +--------------------------------------------------------------------------------------------------------+---+
 * </pre>
 *
 * 4B magic(魔数)   --->yrpc.getBytes()
 * 1B version(版本)   ----> 1
 * 2B header length 首部的长度
 * 4B full length 报文总长度
 * 1B serialize
 * 1B compress
 * 1B requestType
 * 8B requestId
 *
 * body
 *
 * 出站时，第一个经过的处理器
 * @author banyan
 * @createTime 2023-07-02
 */
@Slf4j
public class CrpcResponseEncoder extends MessageToByteEncoder<CrpcResponse> {
    
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CrpcResponse crpcResponse, ByteBuf byteBuf) throws Exception {
        // 4个字节的魔数值
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        // 1个字节的版本号
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        // 2个字节的头部的长度
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        // 总长度不清楚，不知道body的长度 writeIndex(写指针)
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageFormatConstant.FULL_FIELD_LENGTH);
        // 3个类型
        byteBuf.writeByte(crpcResponse.getCode());
        byteBuf.writeByte(crpcResponse.getSerializeType());
        byteBuf.writeByte(crpcResponse.getCompressType());
        // 8字节的请求id
        byteBuf.writeLong(crpcResponse.getRequestId());
        byteBuf.writeLong(crpcResponse.getTimeStamp());
        
        // 1、对响应做序列化
        byte[] body = null;
        if(crpcResponse.getBody() != null) {
            Serializer serializer = SerializerFactory
                .getSerializer(crpcResponse.getSerializeType()).getImpl();
            body = serializer.serialize(crpcResponse.getBody());
    
            // 2、压缩
            Compressor compressor = CompressorFactory.getCompressor(
                crpcResponse.getCompressType()
            ).getImpl();
            body = compressor.compress(body);
        }
        
        if(body != null){
            byteBuf.writeBytes(body);
        }
        int bodyLength = body == null ? 0 : body.length;
        
        // 重新处理报文的总长度
        // 先保存当前的写指针的位置
        int writerIndex = byteBuf.writerIndex();
        // 将写指针的位置移动到总长度的位置上
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
            + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH
        );
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyLength);
        // 将写指针归位
        byteBuf.writerIndex(writerIndex);
    
        if(log.isDebugEnabled()){
            log.debug("响应【{}】已经在服务端完成编码工作。",crpcResponse.getRequestId());
        }
        
    }
    
   
}
