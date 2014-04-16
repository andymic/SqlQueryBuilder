import java.beans.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	static BufferedWriter reportBufferedWriter;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main obj = new Main();
		obj.run();
	}

	// Method available in case user want to connect to a database and execute
	// query
	@SuppressWarnings("unused")
	private Connection connectToDb(String server, String db, String username,
			String password) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection con = null;
		String url = "jdbc:sqlserver://" + server + ";databaseName=" + db + ";";

		try {
			con = DriverManager.getConnection(url, username, password);
			java.sql.Statement s1 = con.createStatement();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	public void run() {
		BufferedReader br = null, breader = new BufferedReader(
				new InputStreamReader(System.in));

		String line = "";
		String cvsSplitBy = ";";// you can change the delimiter of a csv file
		int count = 0;
		boolean created=false;
		try {
			directions();
			System.out.println("Location to create file:");
			String loc = breader.readLine();
			
			PreparedStatement p = null;
			
			System.out.println("Path to csv file:");
			String csvFile =breader.readLine();
			System.out.println("Provide the sql statement to be processed:");
			String sql =breader.readLine();
			br = new BufferedReader(new FileReader(csvFile));
			
			while ((line = br.readLine()) != null)
			{

				String[] row = line.split(cvsSplitBy);
				int counter = 0;
				for (int i = 0; i < sql.length(); i++) {
					if (sql.charAt(i) == '?') {
						counter++;
					}
				}
				if (counter == row.length)
				{
					if(!created)
					{
						createFile(loc);
						created=true;
					}
					String result = buildStatement(sql, row);
					printToFile(result);
					count++;
				}
				else 
				{
					System.out.println("Error:The placeholders in the given string does not match the number of fields in the csv file");
					break;
				}

			}

			System.out.println(count + " rows executed");
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch(IOException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if (br != null) 
			{
				try
				{
					br.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try 
			{
				if(created)
				reportBufferedWriter.close();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void directions() {
		// TODO Auto-generated method stub
		Date cdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		String date = sdf.format(cdate);

		System.out.println("Query Builder  " + date);
		System.out.println("----------------------------------");
		System.out.println("This is a utility tool to build sql queries\n");
		System.out.println("Directions:\n");
		System.out.println("1- Provide the location to create file. Ex: C:\\\\\n");
		System.out.println("2- Name of the file to be created. Ex: test\n");
		System.out.println("3- Provide a path to the csv file. Ex: C:\\\\myfile.csv\n");	
		System.out.println("4- Provide the query to build. Ex: select ?, ? from ? where ? = ?\n");
		System.out.println("*********************************************************************");

	}

	private String buildStatement(String s, String[] row)
	{
		int i = 0;
		StringBuffer sb = new StringBuffer();
		int indexOfRemainder=s.lastIndexOf(63);
	
			Matcher m = Pattern.compile("(\\?)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(s);
			
			while (m.find())
			{
				// New value to insert
				String toInsert = row[i];
				// Append replaced match.
				m.appendReplacement(sb, "" + toInsert);
				i++;
			}
			
			if(indexOfRemainder+1<s.length())
			{
				String remainder=s.substring(indexOfRemainder+1, s.length());
				if(remainder!=null || !(remainder.isEmpty()))
				{
					sb.append(remainder);
				}
			
			}
		
		return sb.toString();
	}

	private void createFile(String filePath) throws IOException {
		String filename;
		String file;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("File Name?");
		file = br.readLine();
		filename = filePath + file + ".txt";
		File ffile = new File(filename);
		
		if (ffile.exists()) {
			System.out.println("The given file name already exist in this directory");
			createFile(filePath);
		}
		else
		{
			reportBufferedWriter = new BufferedWriter(new FileWriter(ffile,true));
		}
	}

	public void printToFile(String line) {
		String sline = System.getProperty("line.separator");

		try
		{
			reportBufferedWriter.write(line + sline);
			reportBufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}