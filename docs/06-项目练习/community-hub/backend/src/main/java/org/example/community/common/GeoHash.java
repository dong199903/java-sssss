package org.example.community.common;

public final class GeoHash {

    private static final char[] BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz".toCharArray();

    private GeoHash() {
    }

    public static String encode(double latitude, double longitude, int precision) {
        double[] latRange = {-90.0, 90.0};
        double[] lonRange = {-180.0, 180.0};
        StringBuilder hash = new StringBuilder(precision);
        boolean even = true;
        int bit = 0;
        int value = 0;
        while (hash.length() < precision) {
            double mid;
            if (even) {
                mid = (lonRange[0] + lonRange[1]) / 2;
                if (longitude >= mid) {
                    value |= 1 << (4 - bit);
                    lonRange[0] = mid;
                } else {
                    lonRange[1] = mid;
                }
            } else {
                mid = (latRange[0] + latRange[1]) / 2;
                if (latitude >= mid) {
                    value |= 1 << (4 - bit);
                    latRange[0] = mid;
                } else {
                    latRange[1] = mid;
                }
            }
            even = !even;
            if (bit < 4) {
                bit++;
            } else {
                hash.append(BASE32[value]);
                bit = 0;
                value = 0;
            }
        }
        return hash.toString();
    }
}
