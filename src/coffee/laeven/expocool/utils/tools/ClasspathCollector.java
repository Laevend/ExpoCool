package coffee.laeven.expocool.utils.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import coffee.laeven.expocool.ExpoCool;
import coffee.laeven.expocool.exception.InvalidFileException;
import coffee.laeven.expocool.utils.Logg;

/**
 * 
 * @author Laeven
 *
 */
public class ClasspathCollector
{
	private final Pattern jarPattern = Pattern.compile(".jar$");
	private final ClassLoader loaderRef;
	private final Path jarRef;
	private CollectMode mode;
	
	/**
	 * Convenient quick method to get a ClasspathCollector
	 * @return ClasspathCollector
	 */
	public static ClasspathCollector collect()
	{
		return new ClasspathCollector();
	}
	
	/**
	 * Convenient quick method to get a ClasspathCollector
	 * @param jar Jar file to be searched
	 * @param loader ClassLoader to use
	 * @return ClasspathCollector
	 */
	public static ClasspathCollector collect(Path jar,ClassLoader loader)
	{
		return new ClasspathCollector(jar,loader);
	}
	
	/**
	 * Create an instance of classpath collector with a .jar and class loader
	 * @param jar Jar file to be searched
	 * @param loader ClassLoader to use
	 */
	public ClasspathCollector(Path jar,ClassLoader loader)
	{
		this.loaderRef = loader;
		this.jarRef = jar;
	}
	
	/**
	 * Create an instance of classpath collector to search this jar
	 */
	public ClasspathCollector()
	{
		this.loaderRef = ExpoCool.class.getClassLoader();
		this.jarRef = ExpoCool.getPluginPath();
	}
	
	/**
	 * Collects a set of classpaths from a jar that are assignable from another class or interface
	 * @param extensionClasses The class(es)/interface(s) that are checked for when searching for classes
	 * @return Set of collected classes
	 * @throws InvalidFileException Thrown when the file passed to this collector is not a .jar
	 */
	public Set<String> getClasspathsAssignableFrom(Class<?>... extensionClasses)
	{
		try
		{
			mode = CollectMode.BY_ASSIGNABLE_FROM;
			return traverseJarForClasses(extensionClasses);
		}
		catch(InvalidFileException e)
		{
			Logg.error("Failed to collect classpaths!",e);
			return Collections.emptySet();
		}
	}
	
	/**
	 * Collects a set of classpaths from a jar that have an annotation
	 * @param anno The annotation that is checked for when searching for classes
	 * @return Set of collected classes
	 * @throws InvalidFileException Thrown when the file passed to this collector is not a .jar
	 */
	public Set<String> getClasspathsWithAnnotation(Class<? extends Annotation> anno)
	{
		try
		{
			mode = CollectMode.BY_ANNOTATION;
			return traverseJarForClasses(anno);
		}
		catch(InvalidFileException e)
		{
			Logg.error("Failed to collect classpaths!",e);
			return Collections.emptySet();
		}
	}
	
	/**
	 * Collects a set of classpaths from a jar that have a specific simple name
	 * @param simpleClassnames The simple name of classes that are searching for
	 * @return Set of collected classes
	 * @throws InvalidFileException Thrown when the file passed to this collector is not a .jar
	 */
	public Set<String> getClasspathsBySimpleName(String... simpleClassnames)
	{
		try
		{
			mode = CollectMode.BY_SIMPLENAME;
			return traverseJarForClasses(simpleClassnames);
		}
		catch(InvalidFileException e)
		{
			Logg.error("Failed to collect classpaths!",e);
			return Collections.emptySet();
		}
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> traverseJarForClasses(Object o) throws InvalidFileException
	{
		if(!jarPattern.matcher(jarRef.getFileName().toString()).find()) { throw new InvalidFileException("File passed is not of type '.jar'"); }
		
		Set<String> classpaths = new HashSet<>();
		
		try(ZipInputStream zis = new ZipInputStream(new FileInputStream(this.jarRef.toAbsolutePath().toString()));)
		{
	        ZipEntry zipEntry = zis.getNextEntry();
	        
	        while(zipEntry != null)
	        {
	        	if(!zipEntry.getName().endsWith(".class")) { zipEntry = zis.getNextEntry(); continue; }
				
				String classpath = zipEntry.getName().replace("/",".");
				
				// This substring method is to remove the '.class' extension on the end
				classpath = classpath.substring(0,classpath.length() - 6);
				               
				try
				{
					Class<?> clazz = Class.forName(classpath,false,loaderRef);
					
					switch(mode)
					{
						// Collect classes that have an annotation
						// Not including the class(s) provided
						case BY_ANNOTATION:
						{
							Class<? extends Annotation> anno = (Class<? extends Annotation>) o;
							
							if(clazz.isAnnotationPresent(anno) && !anno.equals(clazz))
							{
								classpaths.add(classpath);
							}
							break;
						}
						
						// Collect classes that are assignable (extend or implement) from a class/interface
						// Not including the class(s) provided
						case BY_ASSIGNABLE_FROM:
						{
							Class<?>[] exClasses = (Class<?>[]) o;
							
							CheckIfAssignable:
							for(Class<?> eClazz : exClasses)
							{
								if(!eClazz.isAssignableFrom(clazz) || eClazz.equals(clazz)) { continue CheckIfAssignable; }
								
								classpaths.add(classpath);
							}
							break;
						}
						
						// Collect classes by their simple name
						case BY_SIMPLENAME:
						{
							String[] simpNames = (String[]) o;
							
							CheckName:
							for(String className : simpNames)
							{
								if(!clazz.getSimpleName().equals(className)) { continue CheckName; }
								
								classpaths.add(classpath);
							}
							break;
						}
					}
				}
				catch(ClassNotFoundException e)
				{
					Logg.error("Class not found! " + classpath,e);
				}
				
				zipEntry = zis.getNextEntry();
	        }
	        
	        zis.closeEntry();
	        zis.close();
		}
		catch(IOException e)
		{
			Logg.error("An error occured traversing " + this.jarRef.getFileName().toString());
			throw new InvalidFileException("Could not traverse .jar. Perhaps this is not a .jar file or it's corrupt?");
		}
		
		return classpaths;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T initClassNoArgs(String classpath)
	{
		try
		{
			Class<?> clazz = Class.forName(classpath,true,loaderRef);
			Constructor<?> con = clazz.getConstructor();
			return (T) con.newInstance();
		}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Logg.error("Could not instantiate class " + classpath + "!",e);
			return null;
		}
	}
	
	private enum CollectMode
	{
		BY_SIMPLENAME,
		BY_ASSIGNABLE_FROM,
		BY_ANNOTATION
	}
}