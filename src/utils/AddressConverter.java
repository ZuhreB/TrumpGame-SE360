package src.utils;

public class AddressConverter {

    private static final String BASE62_CHARS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;


    private static long ipToLong(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;

        for (int i = 0; i < 4; i++) {
            long octet = Long.parseLong(octets[i]);
            result += octet * Math.pow(256, 3 - i);
        }
        return result;
    }


    private static String longToIp(long ipLong) {
        long tempLong = ipLong;
        int[] numbers = new int[4];

        for (int i = 0; i < 4; i++) {
            numbers[3 - i] = (int)(tempLong % 256);

            tempLong = tempLong / 256;
        }

        StringBuilder address = new StringBuilder();
        for(int i = 0; i < numbers.length; i++){
            address.append(numbers[i]);
            if (i < numbers.length - 1) {
                address.append(".");
            }
        }
        return address.toString();
    }



    public static String addressToCode(String ipAddress) {
        long value = ipToLong(ipAddress);

        StringBuilder sb = new StringBuilder();
        if (value == 0) {
            return BASE62_CHARS.substring(0, 1);
        }

        while (value > 0) {
            sb.insert(0, BASE62_CHARS.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.toString();
    }

    public static String codeToAddress(String code) {
        long value = 0;

        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            int digitValue = BASE62_CHARS.indexOf(c);

            if (digitValue == -1) {
                throw new IllegalArgumentException("Geçersiz Base62 karakteri: " + c);
            }
            value+=Math.pow(62,code.length()-i-1)*digitValue;

        }

        return longToIp(value);
    }

    public static void main(String[] args) {
        String ip1 = "192.168.221.134";

        System.out.println("--- Test 1: " + ip1 + " ---");
        String code1 = addressToCode(ip1);
        System.out.println("Kodlanmış Hali: " + code1);
        String decoded1 = codeToAddress(code1);
        System.out.println("Çözülmüş Hali: " + decoded1);
        System.out.println("Eşleşme: " + ip1.equals(decoded1));
    }
}
