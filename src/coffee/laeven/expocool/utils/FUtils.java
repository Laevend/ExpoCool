package coffee.laeven.expocool.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import coffee.laeven.expocool.utils.security.Bouncer;

/**
 * 
 * @author Laeven
 *
 */
public class FUtils
{
	private static final LocalDateTime epoch = LocalDateTime.ofInstant(Instant.ofEpochMilli(0),ZoneId.systemDefault());
	
	/**
	 * Writes data to a file and overwrites any existing data
	 * @param p Path of file to write data to
	 * @param data Data to write
	 * @return If operation succeeded
	 */
	public static boolean write(Path path,byte[] data)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		Objects.requireNonNull(data,"Data cannot be null!");
		
		if(!createDirectoriesForFile(path)) { return false; }
		
		if(!recursiveDirectoryPermissionCheck(path.getParent(),"rw"))
		{
			Logg.error("Read Write permissions have not been granted for " + path.toAbsolutePath().toString());
			return false;
		}
		
		try
		{
			Files.write(path,data);
			return true;
		}
		catch (IOException e)
		{
			Logg.error("Error occured writing data to " + path.toAbsolutePath().toString(),e);
		}
		
		return false;
	}
	
	/**
	 * Reads data from a file
	 * @param path Path of file to read
	 * @return byte data or null if the file could not be read
	 */
	public static byte[] read(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		if(!Files.exists(path)) { Logg.error("Path " + path.toAbsolutePath().toString() + " does not exist!"); return null; }
		
		if(!recursiveDirectoryPermissionCheck(path.getParent(),"r"))
		{
			Logg.error("Read permissions have not been granted for " + path.toAbsolutePath().toString());
			return null;
		}
		
		try
		{
			byte[] data = Files.readAllBytes(path);
			return data;
		}
		catch (IOException e)
		{
			Logg.error("Error occured reading data from " + path.toAbsolutePath().toString(),e);
		}
		
		return null;
	}
	
	/**
	 * Creates a checksum of a file
	 * @param path Path of file
	 * @return Checksum of file
	 */
	public static long checksumFile(Path path)
	{
		byte[] data = read(path);
		return ChecksumUtils.getChecksum(data);
	}
	
	/**
	 * Creates directories while checking that is has permissions to do so
	 * 
	 * <p>This method assumed the Path being passed is a path to a directory and NOT a file</p>
	 * @param path Path of directory
	 * @return true if directory exists or has been created
	 */
	public static boolean createDirectories(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		// If directory/file exists then there is no point running this method
		if(Files.exists(path)) { return true; }
		
		// Checking lowest directory and working back to a directory that exists to check permissions before creating directory
		if(Files.isDirectory(path))
		{
			return permissionCheck(path,"rwx");
		}
		else
		{
			if(!recursiveDirectoryPermissionCheck(path.getParent(),"rwx")) { return false; }
		}
		
		try
		{
			Files.createDirectories(path);
			return true;
		}
		catch(Exception e)
		{
			Logg.error("Error occured creating new directory!",e);
		}
		
		return false;
	}
	
	/**
	 * Creates parent directories for this file path
	 * 
	 * <p>This method assumed the Path being passed is a path to a file and NOT a directory</p>
	 * @param path Path to a file
	 * @return If directories were created successfully
	 */
	public static boolean createDirectoriesForFile(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		// If directory/file exists then there is no point running this method
		if(Files.exists(path)) { return true; }
		
		// Create directories for file if they don't already exist
		if(!createDirectories(path.getParent())) { return false; }
		return true;
	}
	
	/**
	 * Creates a blank file at this paths location
	 * 
	 * <p>This method assumed the Path being passed is a path to a file and NOT a directory</p>
	 * @param path Path to a file
	 * @return If blank file was created successfully
	 */
	public static boolean createFile(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		// If directory/file exists then there is no point running this method
		if(Files.exists(path)) { return true; }
		
		// Create directories for file if they don't already exist
		if(!createDirectories(path.getParent())) { return false; }
		
		try
		{
			Files.createFile(path);
			return true;
		}
		catch(Exception e)
		{
			Logg.error("Error occured creating new file!",e);
		}
		
		return false;
	}
	
	/**
	 * Creates a blank file with a specific size at this paths location
	 * 
	 * <p>This method assumed the Path being passed is a path to a file and NOT a directory</p>
	 * @param lengthOfFile File size in bytes
	 * @param path Path to a file
	 * @return If blank file was created successfully
	 */
	public static boolean createFile(long lengthOfFile,Path path)
	{
		Bouncer.requireNotNullAndInRange(lengthOfFile,0,Long.MAX_VALUE,"Length of file cannot be null and must be a positive long value!");
		Objects.requireNonNull(path,"Path cannot be null!");
		
		if(Files.exists(path)) { return true; }
		if(!createDirectories(path.getParent())) { return false; }
		
		RandomAccessFile f = null;
		
		try
		{
			f = new RandomAccessFile(path.toString(),"rw");
			f.setLength(lengthOfFile);
			f.close();
			return true;
		}
		catch(Exception e)
		{
			Logg.error("Error occured. File of path '" + path.toString() + "' could not be created!",e);
		}
		
		return false;
	}
	
	public static boolean createFile(long lengthOfFile,Path path,LocalDateTime lastModifiedTime,LocalDateTime lastAccessTime,LocalDateTime createTime)
	{
		Bouncer.requireNotNullAndInRange(lengthOfFile,0,Long.MAX_VALUE,"Length of file cannot be null and must be a positive long value!");
		Objects.requireNonNull(path,"Path cannot be null!");
		Objects.requireNonNull(lastModifiedTime,"Last modified time cannot be null!");
		Objects.requireNonNull(lastAccessTime,"Local Date Time be null!");
		Objects.requireNonNull(createTime,"Local Date Time cannot be null!");
		
		if(!createFile(lengthOfFile,path)) { return false; }
		
		BasicFileAttributeView attr = Files.getFileAttributeView(path,BasicFileAttributeView.class);
		FileTime lastModTime = FileTime.fromMillis(lastModifiedTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		FileTime lastAccTime = FileTime.fromMillis(lastAccessTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		FileTime creationTime = FileTime.fromMillis(createTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		try
		{
			attr.setTimes(lastModTime,lastAccTime,creationTime);
			return true;
		}
		catch(Exception e)
		{
			Logg.error("Error occured. Could not change file times",e);
		}
		
		return false;
	}
	
	/**
	 * If a directory doesn't exist keep going up in the directory structure until a directory
	 * that exists is found so permissions can be checked before creating new directories
	 * @param path Path of a directory
	 * @param permissions Permissions to check @see {@link FUtils#permissionCheck(Path, String)}
	 * @return True if a directory was found and the JVM has the permissions requested. False if a directory was not found and/or inadequate permissions
	 */
	public static boolean recursiveDirectoryPermissionCheck(Path path,String permissions)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		Objects.requireNonNull(permissions,"Permissions cannot be null!");
		
		if(Files.isDirectory(path))
		{
			return permissionCheck(path,permissions);
		}
		else
		{
			return recursiveDirectoryPermissionCheck(path.getParent(),permissions);
		}
	}
	
	/**
	 * Attempts to retrieve the creation date of a file.
	 * 
	 * <p>If this fails, the epoch date (1/1/1970) is returned instead
	 * @param p File path
	 * @return
	 */
	public static LocalDateTime getFileCreationDate(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		if(!Files.exists(path)) { return epoch; }
		if(!permissionCheck(path,"r")) { return epoch; }
		
		try
		{
			BasicFileAttributes attr = Files.readAttributes(path,BasicFileAttributes.class);
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(attr.creationTime().toMillis()),ZoneId.systemDefault());
		}
		catch(Exception e)
		{
			Logg.error("Error occured retrieving file " + path.getFileName() + " creation date!",e);
			return epoch;
		}	
	}
	
	/**
	 * Attempts to retrieve the last modified date of a file.
	 * 
	 * <p>If this fails, the epoch date (1/1/1970) is returned instead
	 * @param p File path
	 * @return
	 */
	public static LocalDateTime getFileLastModifiedDate(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		if(!Files.exists(path)) { return epoch; }
		if(!permissionCheck(path,"r")) { return epoch; }
		
		try
		{
			BasicFileAttributes attr = Files.readAttributes(path,BasicFileAttributes.class);
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(attr.lastModifiedTime().toMillis()),ZoneId.systemDefault());
		}
		catch(Exception e)
		{
			Logg.error("Error occured retrieving file " + path.getFileName() + " modified date!",e);
			return epoch;
		}	
	}
	
	/**
	 * Attempts to retrieve the last access date of a file.
	 * 
	 * <p>If this fails, the epoch date (1/1/1970) is returned instead
	 * @param p File path
	 * @return
	 */
	public static LocalDateTime getFileLastAccessDate(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		if(!Files.exists(path)) { return epoch; }
		if(!permissionCheck(path,"r")) { return epoch; }
		
		try
		{
			BasicFileAttributes attr = Files.readAttributes(path,BasicFileAttributes.class);
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(attr.lastAccessTime().toMillis()),ZoneId.systemDefault());
		}
		catch(Exception e)
		{
			Logg.error("Error occured retrieving file " + path.getFileName() + " last access date!",e);
			return epoch;
		}	
	}
	
	/**
	 * Runs a permission check on a file to see if the JVM has permissions to read, write or execute.
	 * @param p Path to the file/directory
	 * @param permissions The permissions to be checked if granted. Entered as: r or rw or rwx
	 * @return
	 */
	public static boolean permissionCheck(Path path,String permissions)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		Objects.requireNonNull(permissions,"Permissions cannot be null!");
		
		boolean hasAllPermissions = true;
		
		for(char perm : permissions.toCharArray())
		{
			switch(perm)
			{
				case 'r':
				{
					if(!Files.isReadable(path)) { Logg.error("Required READ permission has not been granted for file " + path.getFileName()); hasAllPermissions = false; }
					break;
				}
				case 'w':
				{
					if(!Files.isWritable(path)) { Logg.error("Required WRITE permission has not been granted for file " + path.getFileName()); hasAllPermissions = false; }
					break;
				}
				case 'x':
				{
					if(!Files.isExecutable(path)) { Logg.error("Required EXECUTE permission has not been granted for file " + path.getFileName()); hasAllPermissions = false; }
					break;
				}
			}
		}
		
		return hasAllPermissions;
	}
	
	public static void delete(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		if(!Files.exists(path)) { return; }
		if(!permissionCheck(path,"rw")) { return; }
		
		if(Files.isRegularFile(path))
		{
			try
			{
				Files.delete(path);
				
				if(Files.exists(path))
				{
					Logg.error("Error occured attempting to delete directory: " + path.toString());
				}
			}
			catch (IOException e)
			{
				Logg.error("Error occured attempting to delete directory: " + path.toString(),e);
			}
			
			return;
		}
		
		int files = walkAndCount(path);
		
		if(files == 0) { Logg.info("No Files were found to be deleted."); return; }
		
		deleteFiles(path,files);
	}
	
	public static int walkAndCount(Path path)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		
		AtomicInteger numOfFiles = new AtomicInteger(0);
		
		try
		{
			Files.walkFileTree(path,new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult postVisitDirectory(Path dir,IOException exc) throws IOException
				{
					if(!permissionCheck(dir,"r")) { return FileVisitResult.CONTINUE; }
					numOfFiles.incrementAndGet();
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException
				{
					if(!permissionCheck(file,"r")) { return FileVisitResult.CONTINUE; }
					numOfFiles.incrementAndGet();
					return FileVisitResult.CONTINUE;
				}
			});
			
			return numOfFiles.get();
		}
		catch(Exception e)
		{
			Logg.error("Error occured trying to map directory: " + path.toString(),e);
		}
		
		return 0;
	}
	
	private static void deleteFiles(Path path,int numOfFiles)
	{
		Objects.requireNonNull(path,"Path cannot be null!");
		Bouncer.requireNotNullAndInRange(numOfFiles,0,Integer.MAX_VALUE,"Number of files cannot be null and must be a positive int value!");
		
		try
		{
			Files.walkFileTree(path,new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult postVisitDirectory(Path dir,IOException exc) throws IOException
				{
					if(!permissionCheck(dir,"rw")) { return FileVisitResult.CONTINUE; }
					
					Files.delete(dir);
					
					if(Files.exists(dir))
					{
						Logg.error("Error occured attempting to delete directory: " + dir.toString());
					}
					
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException
				{
					if(!permissionCheck(file,"rw")) { return FileVisitResult.CONTINUE; }
					
					Files.delete(file);
					
					if(Files.exists(file))
					{
						Logg.error("Error occured attempting to delete file: " + file.toString());
					}
					
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch(Exception e)
		{
			Logg.error("Error occured attempting to delete: " + path.toString(),e);
		}
	}
	
	public static void copyFile(Path src,Path dest)
	{
		Objects.requireNonNull(src,"Src path cannot be null!");
		Objects.requireNonNull(dest,"Dest path cannot be null!");
		
		try(InputStream is = new FileInputStream(src.toString()); OutputStream os = new FileOutputStream(dest.toString()))
		{
			byte[] buffer = new byte[8192];
			int length;
			
			while((length = is.read(buffer)) > 0)
			{
				os.write(buffer,0,length);
			}
		}
		catch(Exception e)
		{
			Logg.error("Error occured copying file " + src.getFileName() + " from " + src.toString() + " to " + dest.toString(),e);
		}
	}
	
	/**
	 * The DirectoryStream<T> interface can be used to iterate over a directory without preloading its content into memory. 
	 * While the old API creates an array of all filenames in the folder, the new approach loads each filename 
	 * (or limited size group of cached filenames) when it encounters it during iteration.
	 */
	
	/**
	 * 
	 * @param directory
	 * @return
	 */
	public static List<Path> getPathsInDirectory(Path directory)
	{
		Objects.requireNonNull(directory,"Directory path cannot be null!");
		
		List<Path> paths = new ArrayList<>();
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory))
		{
			for(Path entry : stream)
			{
				paths.add(entry);
			}
		}
		catch(Exception e)
		{
			Logg.error("Could not stream directory " + directory.toString(),e);
		}
		
		return paths;
	}
}