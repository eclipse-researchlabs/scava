package org.eclipse.scava.platform.communicationchannel.mbox.downloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.eclipse.scava.platform.logging.OssmeterLogger;
import org.eclipse.scava.platform.storage.communicationchannel.CommunicationChannelDataPath;

public class TgzExtractor {

	private final static int BUFFER_SIZE = 2048; 
	private static OssmeterLogger logger;
	
	static
	{
		logger = (OssmeterLogger) OssmeterLogger.getLogger("platform.communicationchannel.mbox.downloader");
	}
	
		
	public static CommunicationChannelDataPath extract(Path tempDir, InputStream inputStream , String ext) throws IOException {
		
		File file = createTempFile(tempDir,inputStream, ext);
		CommunicationChannelDataPath dataPath = null;
		
		if(file==null)
			return dataPath;
		
		//No compression
		if(ext.equals(".mbox") || ext.equals(".txt"))
		{
			dataPath= new CommunicationChannelDataPath(file.toPath(), true);
		}
		else
		{
		    try
		    {
		    	InputStream in = new FileInputStream(file);
				try
				{
			        if(ext.equals(".txt.gz") || ext.equals(".mbox.gz"))
			        {
			        	GZIPInputStream gzis = new GZIPInputStream(in);
			        	File textfile = createTempFile(tempDir, gzis, ".txt");
				    	dataPath = new CommunicationChannelDataPath(textfile.toPath(), true);
			        }
			        else
			        {
			        	GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(in);
			        	try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn))
			        	{
			        		String rootCreated="";
			    	        TarArchiveEntry entry;
			    	        
			                HashMap<String, String> mappingFolders = new HashMap<String, String> ();
			                
			                Path path;
			                String pathUntilNow;
	
			                while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
			                    /** If the entry is a directory, create the directory. **/
			                    if (!entry.isDirectory() && !entry.getName().contains("/.")) {
			                        List<String> parsedPath = pathParser(entry.getName());
			                        pathUntilNow="";
			                        for(int i=0; i<parsedPath.size()-1; i++)
			                        {
			                       	
			                            if(parsedPath.get(i).isEmpty())
			                                continue;
			                            if(mappingFolders.containsKey(parsedPath.get(i)))
			                            {
			                                pathUntilNow =mappingFolders.get(parsedPath.get(i));
			                            }
			                            else
			                            {
			                                if(rootCreated.isEmpty())
			                                {
			                                    path=Files.createTempDirectory(parsedPath.get(i) + "_");
			                                    rootCreated=tempDir.toString()+"/"+path.toString();
			                                }
			                                else
			                                {
			                                    path=Files.createTempDirectory(Paths.get(pathUntilNow), parsedPath.get(i));                                
			                                }
			                                mappingFolders.put(parsedPath.get(i), path.toString());
			                                pathUntilNow =path.toString();
			                            }
			                        }
			                        path = createTempFileInTemporalDirectory(parsedPath.get(parsedPath.size()-1), pathUntilNow);
			                        
			                        int count;
			                        byte data[] = new byte[BUFFER_SIZE];
			                        FileOutputStream fos = new FileOutputStream(path.toFile(), false);
			                        try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE)) {
			                            while ((count = tarIn.read(data, 0, BUFFER_SIZE)) != -1) {
			                                dest.write(data, 0, count);
			                            }
			                        }
			                    }
			                }
			                dataPath = new CommunicationChannelDataPath(Paths.get(rootCreated), false);
			    	    } catch (IOException e) {
			    	    	logger.error("Error while reading Tar file", e);
			    		}
			        	gzipIn.close();
			        }
					
				}
				catch (IOException e) {
					logger.error("Error while reading GZIP file", e);
				}
				
				in.close();
				file.delete();
				
			}
		    catch (FileNotFoundException e) {
				logger.error("Error while reading temporal file",e);
			}
		}
	    return dataPath;
	    
	}
	
	private static File createTempFile(Path tempDir, InputStream inputStream, String extension) throws IOException {
		File tmpFile = createEmptyTempFile(tempDir, extension);
	    try {
	        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
	        IOUtils.copy(inputStream, bufferedOutputStream);
	        bufferedOutputStream.flush();
	        bufferedOutputStream.close();
	        return tmpFile;
	    }
	    catch (IOException e)
	    {
	    	logger.error("Error while creating temporal file", e);
	    	if (tmpFile != null) {
	            tmpFile.delete();
	        }
	        throw e;
	    }
	}
	
	private static File createEmptyTempFile(Path tempDir, String extension) throws IOException
	{
		String uniqueName = UUID.randomUUID().toString() + extension;
		return new File(tempDir.toString()+"/"+uniqueName);
	}
	
	private static List<String> pathParser(String path)
	{
	    List<String> pathParsed = Arrays.asList(path.split("/"));
	    return pathParsed;
	}
	
	private static Path createTempFileInTemporalDirectory(String fileName, String pathUntilNow) throws IOException
	{
		
	    String[] fileNameParsed = fileName.split("\\.");
	    String name = fileNameParsed[0] + "_";
	    String extension="";
	    for(int i=1; i<fileNameParsed.length; i++)
	        extension+=fileNameParsed[i];
	    
	    if(!(extension.isEmpty()))
	    	extension = "."+ extension;
	    	
	    return Files.createTempFile(Paths.get(pathUntilNow), name, extension);
	}

}
