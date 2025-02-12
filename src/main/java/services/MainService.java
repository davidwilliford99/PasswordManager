package services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;



public class MainService {
	
	private static final String FILE_PATH = "properties.txt";	
	
	
	
	/**
	 * @description  Checks if application's first start
	 * 
	 * @return
	 */
	public static boolean isFirstStart() {
		try 
		{
			List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));	
			String currentFirstStartVal = lines.get(0).split("=")[1].trim();
	
            return currentFirstStartVal.equals("true");                                             
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	/**
	 *  @title       Bootstrap 
	 *  
	 *  @description Boot straps the project. 
	 *  @description Check and update 'first_start' value in properties.txt
	 *  @description Create SQLite tables if first_value = true
	 *  
	 *  @return      true if first start, false if not
	 */
	public static void bootstrap(String userKey) {  
        DatabaseService.tableSetup(userKey); 
        setFirstStart(false);
	}
	
	
	
	/**
	 * @description  Sets value of first_start in properties.text
	 * 
	 * @param        value  (boolean)
	 */
	private static void setFirstStart(boolean value) {
		try 
		{
			List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
			
			String propertyValue        = lines.get(0).split("=")[0].trim();
			String newLine = propertyValue + "=" + value;
			
			Files.write(
					Paths.get(FILE_PATH), 
					newLine.getBytes(), 
					StandardOpenOption.TRUNCATE_EXISTING, 
					StandardOpenOption.WRITE
			);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
