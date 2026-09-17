package org.example.learn.other;

/**
 * 长度前缀拆包：TCP 是字节流，应用层必须自己定边界。
 */
public final class TcpFrameDemo {

    public static void run() {
        byte[] stream = concat(
                frame("hello"),
                frame("world"),
                new byte[] {0, 3, 'p', 'a'} // 半包：声明 3 字节，只到了 2 个
        );
        int i = 0;
        while (i + 2 <= stream.length) {
            int len = ((stream[i] & 0xff) << 8) | (stream[i + 1] & 0xff);
            if (i + 2 + len > stream.length) {
                System.out.println("[半包] 需要 " + len + " 字节，缓冲区不够，等下一次读");
                break;
            }
            String msg = new String(stream, i + 2, len);
            System.out.println("[整包] " + msg);
            i += 2 + len;
        }
        System.out.println("Netty: LengthFieldBasedFrameDecoder。粘包=多个包连在一起，拆包=半包。");
    }

    private static byte[] frame(String s) {
        byte[] body = s.getBytes();
        byte[] out = new byte[2 + body.length];
        out[0] = (byte) (body.length >> 8);
        out[1] = (byte) body.length;
        System.arraycopy(body, 0, out, 2, body.length);
        return out;
    }

    private static byte[] concat(byte[]... parts) {
        int n = 0;
        for (byte[] p : parts) {
            n += p.length;
        }
        byte[] all = new byte[n];
        int i = 0;
        for (byte[] p : parts) {
            System.arraycopy(p, 0, all, i, p.length);
            i += p.length;
        }
        return all;
    }
}
