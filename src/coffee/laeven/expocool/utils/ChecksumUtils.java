package coffee.laeven.expocool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * Class for checksums
 * 
 * TODO Add implementation for larger files
 */
public class ChecksumUtils
{
	public static long getChecksum(byte[] bytes)
	{
		Checksum crc32 = new CRC32();
		crc32.update(bytes,0,bytes.length);
		return crc32.getValue();
	}
	
	public static long getChecksum(InputStream stream,int bufferSize) throws IOException
	{
		CheckedInputStream cis = new CheckedInputStream(stream,new CRC32());
		byte[] buffer = new byte[bufferSize];
		
		while(cis.read(buffer,0,buffer.length) >= 0) {}
		
		return cis.getChecksum().getValue();
	}
}
