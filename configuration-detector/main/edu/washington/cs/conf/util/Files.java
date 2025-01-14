package edu.washington.cs.conf.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Files {
  private Files() {
    throw new IllegalStateException("no instances");
  }
  
  public static boolean checkDirExistence(String path) {
	  File f = new File(path);
	  if(!f.isDirectory()) {
		  return false;
	  }
	  return f.exists();
  }
  
  public static boolean checkFileExistence(String path) {
	  File f = new File(path);
	  if(f.isDirectory()) {
		  return false;
	  }
	  return f.exists();
  }
  
  public static int countLinesFast(String filename) {
	  InputStream is = null;
	    try {
	    	is = new BufferedInputStream(new FileInputStream(filename));
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } catch (Throwable e) {
	    	throw new Error(e);
	    } finally {
	    	try {
	    		if(is != null) {
	                is.close();
	    		}
	    	} catch (Throwable ee) {
	    		throw new Error(ee);
	    	}
	    }
	}
  
  public static boolean createIfNotExistNoExp(String path) {
	  try {
		  return createIfNotExist(path);
	  } catch (IOException e) {
		  throw new RuntimeException(e);
	  }
  }
  
  public static boolean createIfNotExist(String path) throws IOException {
	  return createIfNotExist(new File(path));
  }
  
   public static boolean createIfNotExist(File f) throws IOException {
	  if(f.exists()) {
		  return false;
	  }
	  return f.createNewFile();
  }
  
  // Deletes all files and subdirectories under dir.
  // Returns true if all deletions were successful.
  // If a deletion fails, the method stops attempting to delete and returns false.
  // Attempts to detect symbolic links, and fails if it finds one.
  public static boolean deleteRecursive(File dir) {
    if (dir == null) throw new IllegalArgumentException("dir cannot be null.");
    String canonicalPath = null;
    try {
      canonicalPath = dir.getCanonicalPath();
    } catch (IOException e) {
      System.out.println("IOException while obtaining canonical file of " + dir);
      System.out.println("Will not delete file or its children.");
      return false;
    }
    if (!canonicalPath.equals(dir.getAbsolutePath())) {
      System.out.println("Warning: potential symbolic link: " + dir);
      System.out.println("Will not delete file or its children.");
      return false;
    }
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i=0; i<children.length; i++) {
        boolean success = deleteRecursive(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }
  
   public static List<File> getFileListing(File aStartingDir, String suffix) throws FileNotFoundException {
	   if(suffix == null) {
		   return getFileListing(aStartingDir);
	   }
	   List<File> files = getFileListing(aStartingDir);
	   List<File> retFiles = new LinkedList<File>();
	   for(File f : files) {
		   if(f.getName().endsWith(suffix)) {
			   retFiles.add(f);
		   }
	   }
	   return retFiles;
   }
  
	public static List<File> getFileListing(File aStartingDir)
			throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result);
		return result;
	}

	static private List<File> getFileListingNoSort(File aStartingDir)
			throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File file : filesDirs) {
			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be
	 * read.
	 */
	static private void validateDirectory(File aDirectory)
			throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: "
					+ aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: "
					+ aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: "
					+ aDirectory);
		}
	}

  public static List<String> findFilesInDir(String dirPath, String startsWith, String endsWith) {
	  return findFilesInDir(new File(dirPath), startsWith, endsWith);
  }
  
  public static List<String> findFilesInDir(File dir, String startsWith, String endsWith) {
    if (!dir.isDirectory()) throw new IllegalArgumentException("not a directory: " + dir.getAbsolutePath());
    File currentDir = dir;
    List<String> retval = new ArrayList<String>();
    for (String fileName : currentDir.list()) {
      if ((startsWith == null || fileName.startsWith(startsWith))
    		  && (endsWith == null || fileName.endsWith(endsWith)))
        retval.add(fileName);
    }
    return retval;
  }
  
  public static String mergeFilesNoExp(String[] sourceFiles, String destFile) {
	  try {
		  return mergeFiles(sourceFiles, destFile);
	  } catch (Throwable e) {
		  e.printStackTrace();
		  throw new Error(e);
	  }
  }
  
  //append the file from source to dest
  public static void appendFile(String sourceFile, String destFile) {
	  String content = Files.readWholeAsString(sourceFile);
	  try {
	      Files.writeToFile(Globals.lineSep, destFile, true);
	      Files.writeToFile(content, destFile, true);
	  } catch (Throwable e) {
		  throw new RuntimeException(e);
	  }
  }
  
  //all file must be text file
  public static String mergeFiles(String[] sourceFiles, String destFile) throws IOException {
	  for(String fName : sourceFiles) {
		  File f = new File(fName);
		  checkFileExistence(f.getAbsolutePath());
	  }
	  createIfNotExist(destFile);
	  //the writer for the dest file
	  BufferedWriter writer= new BufferedWriter(new FileWriter(new File(destFile)));
	  //start to copy contents to the destination file
	  for(String fName : sourceFiles) {
		  File f = new File(fName);
		  BufferedReader reader = new BufferedReader(new FileReader(f));
		  String line= reader.readLine();
		  while(line != null) {
		      writer.write(line);
		      writer.write(Globals.lineSep);
		      line= reader.readLine();
		  }
	  }
	  
	  return destFile;
  }
  
  public static void copyFileNoExp(String srcFilePath, String destFilePath) {
	  copyFileNoExp(new File(srcFilePath), new File(destFilePath));
  }
  
  public static void copyFileNoExp(File sourceFile, File destFile) {
	  try {
		  copyFile(sourceFile, destFile);
	  } catch (IOException e) {
		  throw new Error(e);
	  }
  }
  
  public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }
	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}

  public static void writeToFile(String s, File file) throws IOException {
    writeToFile(s, file, false);
  }

  public static void writeToFile(String s, String fileName) throws IOException {
    writeToFile(s, fileName, false);
  }
  
  public static void emptyFileNoExp(String fileName) {
	  writeToFileNoExp("", fileName);
  }
  
  public static void writeToFileNoExp(String s, String fileName)  {
	  try {
		Files.createIfNotExist(fileName);
		writeToFile(s, fileName);
	  } catch (IOException e) {
		e.printStackTrace();
	  }
  }

  public static void writeToFile(String s, File file, Boolean append) throws IOException {
    BufferedWriter writer= new BufferedWriter(new FileWriter(file, append));
    try{
      writer.append(s);
    } finally {
      writer.close();
    }        
  }

  public static void appendToFile(String s, String fileName) {
	  try {
		  writeToFile(s, fileName, true);
	  } catch (Throwable e) {
		  throw new RuntimeException(e);
	  }
  }
  
  public static void writeToFile(String s, String fileName, Boolean append) throws IOException {
    writeToFile(s, new File(fileName), append);
  }

  /**
   * Reads the whole file. Does not close the reader.
   * Returns the list of lines.  
   */
  public static List<String> readWhole(BufferedReader reader) throws IOException {
    List<String> result= new ArrayList<String>();
    String line= reader.readLine();
    while(line != null) {
      result.add(line);
      line= reader.readLine();
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * Reads the whole file. Returns the list of lines.  
   */
  public static List<String> readWhole(String fileName) throws IOException {
    return readWhole(new File(fileName));
  }
  
  /**
   * Reads the whole file. Returns the list of lines.  
   */
  public static List<String> readWholeNoExp(String fileName) {
	  try {
         return readWhole(new File(fileName));
	  }  catch (IOException e) {
		  throw new RuntimeException(e);
	  }
  }
  
  /**
   * Reads the whole file. Returns the file content as a flat String
   * */
  public static String readWholeAsString(String fileName) {
	  List<String> content = readWholeNoExp(fileName);
	  StringBuilder sb = new StringBuilder();
	  for(String str : content) {
		  sb.append(str);
		  sb.append(Globals.lineSep);
	  }
	  return sb.toString();
  }

  /**
   * Reads the whole file. Returns the list of lines.  
   */
  public static List<String> readWhole(File file) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(file));
    try{
      return readWhole(in);
    } finally{
      in.close();
    }
  }    

  /**
   * Reads the whole file. Returns the list of lines.
   * Does not close the stream.  
   */
  public static List<String> readWhole(InputStream is) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    return readWhole(in);
  }

  /**
   * Reads the whole file. Returns one big String.
   */
  public static String getFileContents(File file) throws IOException {
    StringBuilder result = new StringBuilder();    
    Reader in = new BufferedReader(new FileReader(file));
    try{
      int c;
      while ((c = in.read()) != -1)
      {
        result.append((char)c);
      }
      in.close();
      return result.toString();
    } finally{
      in.close();
    }
  }
  
  public static String getFileContents(Reader in) throws IOException {
	    StringBuilder result = new StringBuilder();  
	    try{
	      int c;
	      while ((c = in.read()) != -1)
	      {
	        result.append((char)c);
	      }
	      in.close();
	      return result.toString();
	    } finally{
	      in.close();
	    }
	  }

  /**
   * Reads the whole file. Returns one big String.
   */
  public static String getFileContents(String path) throws IOException {
    return getFileContents(new File(path));
  }

  public static LineNumberReader getFileReader(String fileName) {
    return getFileReader(new File(fileName));
  }

  public static LineNumberReader getFileReader(File fileName) {
    LineNumberReader reader;
    try {
      reader = new LineNumberReader(new BufferedReader(
          new FileReader(fileName)));
    } catch (FileNotFoundException e1) {
      throw new IllegalStateException("File was not found " + fileName + " " + e1.getMessage());
    }
    return reader;
  }
  public static String addProjectPath(String string) {  
    return System.getProperty("user.dir") + File.separator + string;
  }

  public static boolean deleteFile(String path) {
    File f = new File(path);
    return f.delete();
  }
  
	public static void deleteDirectory(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				deleteDirectory(c);
		}
		if (!f.delete()) {
			throw new Error("Failed to delete file: " + f);
		}

	}
  
  public static File createTempDirectoryNoExp() {
	  try {
		  return createTempDirectory();
	  } catch (Throwable e) {
		  throw new Error(e);
	  }
  }
  
	public static File createTempDirectory() throws IOException {
		final File temp;
		temp = File.createTempFile("conf_evol_proj", Long.toString(System.nanoTime()));

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}
		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}

  /**
   * Reads a single long from the file.
   * Returns null if the file does not exist.
   * @throws  IllegalStateException is the file contains not just 1 line or
   *          if the file contains something.
   */
  public static Long readLongFromFile(File file) {
    if (! file.exists())
      return null;
    List<String> lines;
    try {
      lines = readWhole(file);
    } catch (IOException e) {
      throw new IllegalStateException("Problem reading file " + file + " ", e);
    }
    if (lines.size() != 1)
      throw new IllegalStateException("Expected exactly 1 line in " + file + " but found " + lines.size());
    try{
      return Long.valueOf(lines.get(0));
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Expected a number (type long) in " + file + " but found " + lines.get(0));
    }
  }

  /**
   * Prints out the contents of the give file to stdout.
   */
  public static void cat(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line = reader.readLine();
      while (line != null) {
        System.out.println(line);
        line = reader.readLine();
      }
      reader.close();
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  /**
   * Returns the number of lines in the given file.
   */
  public static int countLines(String filename) {
    int lines = 0;
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line = reader.readLine();
      while (line != null) {
        lines++;
        line = reader.readLine();
      }
      reader.close();
    } catch (Exception e) {
      throw new Error(e);
    }
    return lines;
  }
  
  /**
   * Fetch a certain line
   * */
  public static String fetchLineInFile(String sourceDir, String fullClassName, int lineNum) {
		Utils.checkTrue(Files.checkDirExistence(sourceDir));
		if(lineNum <= 0) {
			return "Source_Not_Available line num <= 0";
		}
		if(fullClassName.indexOf("$") != -1) {
			return "Source_Not_Available for anonymous class: " + fullClassName;
		}
		String filePath = sourceDir + Globals.fileSep + fullClassName.replace('.', File.separatorChar) + ".java";
		File f = new File(filePath);
		if(!f.exists()) {
			return "File: " + filePath + " does not exist";
		}
		List<String> content = Files.readWholeNoExp(filePath);
		if(content.size() <= lineNum) {
			return "There are: " + content.size() + " lines in: " + filePath + ", but you are requesting: " + lineNum;
		}
		return content.get(lineNum - 1).trim();
		//FIXME, may have memory leak here, cannot clear content
	}
}
