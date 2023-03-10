package it.spaghettisource.broadcastsync.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDTest {

	private static Logger  log = LoggerFactory.getLogger(UUIDTest.class);

	@DisplayName("UUID seralization/deserialization")
	@Test
	void testUUID() {
		assertEquals(2,2);

		UUID uuid = UUID.randomUUID();

		log.info("original UUID generated:"+uuid);

		byte[] serailized = serializeUUID(uuid);

		UUID des_uuid =deserializeUUID(serailized);
		
		log.info("deserialized UUID generated:"+des_uuid);		

		assertEquals(uuid.toString(), des_uuid.toString());
	}


	public byte[] serializeUUID(UUID uuid) {
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		byte[] uuidByteArray = new byte[16];
		for (int i = 0; i < 8; i++) {
			uuidByteArray[i] = (byte) (mostSignificantBits >>> 8 * (7 - i));
			uuidByteArray[i + 8] = (byte) (leastSignificantBits >>> 8 * (7 - i));
		}

		return uuidByteArray;

	}

	public UUID deserializeUUID(byte[] uuidBytes) {
		long mostSignificantBits = 0;
		long leastSignificantBits = 0;
		for (int i = 0; i < 8; i++) {
			mostSignificantBits |= (long) (uuidBytes[i] & 0xFF) << (8 * (7 - i));
			leastSignificantBits |= (long) (uuidBytes[i + 8] & 0xFF) << (8 * (7 - i));
		}
		return new UUID(mostSignificantBits, leastSignificantBits);

	}


}
