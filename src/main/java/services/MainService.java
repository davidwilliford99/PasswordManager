package services;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MainService {
	
	private static final String FILE_PATH = "src/main/resources/properties.txt";	
	
	
	
	/**
	 * 
	 *  @title       Bootstrap 
	 *  
	 *  @description Boot straps the project. 
	 *  @description Check and update 'first_start' value in properties.txt
	 *  @description Create SQLite tables if first_value = true
	 *  
	 *  @return      true if first start, false if not
	 *  
	 */
	public static boolean bootstrap(String userKey) {
		try 
		{
			List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
			
			String propertyKey          = lines.get(0).split("=")[0].trim();
			String currentFirstStartVal = lines.get(0).split("=")[1].trim();
			
            if (currentFirstStartVal.equals("true")) {
            	
                FileWriter writer = new FileWriter(FILE_PATH, false);
                writer.write(propertyKey + "=false");                     // write first_start=false
                writer.close();
                
                DatabaseService.tableSetup(userKey);   
                return true;
            }
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;  // if not first start
	}

}
