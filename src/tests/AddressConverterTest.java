package src.tests;

import org.junit.Test;
import static org.junit.Assert.*;
import src.utils.AddressConverter;

public class AddressConverterTest {

    @Test
    public void testAddressToCodeAndBack() {
        String originalIp = "192.168.1.105";

        String code = AddressConverter.addressToCode(originalIp);
        assertNotNull(code);
        assertFalse(code.isEmpty());

        // Decode
        String decodedIp = AddressConverter.codeToAddress(code);
        assertEquals(originalIp, decodedIp);
    }

    @Test
    public void testCornerCaseIp() {
        String ip1 = "0.0.0.1";
        assertEquals(ip1, AddressConverter.codeToAddress(AddressConverter.addressToCode(ip1)));

        String ip2 = "255.255.255.255";
        assertEquals(ip2, AddressConverter.codeToAddress(AddressConverter.addressToCode(ip2)));
    }
}
