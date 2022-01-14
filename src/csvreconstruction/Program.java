package csvreconstruction;

import com.stenway.sml.SmlAttribute;
import com.stenway.sml.SmlElement;
import com.stenway.sml.SmlNode;
import com.stenway.sml.SmlStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;

public class Program {

	private static String getCsvLine(SmlAttribute attribute) {
		String[] attributeValues = attribute.getValues();
		String attributeName = attribute.getName();
		String[] csvValues = new String[attributeValues.length+1];
		csvValues[0] = attributeName;
		for (int i=0; i<attributeValues.length; i++) {
			csvValues[i+1] = attributeValues[i];
		}
		String result = String.join(",", csvValues);
		return result;
	}
	
	private static void saveCsvWin(Collection<String> lines, String filePath) throws IOException {
		String result = String.join("\r\n", lines);
		Files.write(Paths.get(filePath), result.getBytes());
	}
	
	public static void main(String[] args) {
		try {
			String inputSmlFilePath = "D:\\RKICsvSequenz\\Sequenz.sml";
			String outputCsvFilePath = "D:\\RKICsvSequenz\\Reconstructed.csv";
			
			LinkedHashSet<String> reconstructedLines = new LinkedHashSet<String>();
			
			SmlStreamReader streamReader = new SmlStreamReader(inputSmlFilePath);
			while (true) {
				SmlNode node = streamReader.readNode();
				if (node == null) { break; }
				if (!(node instanceof SmlElement)) {
					throw new RuntimeException("Wrong format");
				}
				SmlElement element = (SmlElement)node;
				int removedCount = 0;
				int addedCount = 0;
				if (element.hasElement("Removed")) {
					SmlElement removedElement = element.element("Removed");
					SmlAttribute[] removeItems = removedElement.attributes();
					removedCount = removeItems.length;
					for (SmlAttribute removeItem : removeItems) {
						String csvLine = getCsvLine(removeItem);
						reconstructedLines.remove(csvLine);
					}
				}
				
				SmlElement addedElement = element.element("Added");
				SmlAttribute[] addedItems = addedElement.attributes();
				addedCount = addedItems.length;
				for (SmlAttribute addedItem : addedItems) {
					String csvLine = getCsvLine(addedItem);
					reconstructedLines.add(csvLine);
				}
				
				String name = element.getName();
				System.out.println(name + " Removed: " + removedCount + " Added: " + addedCount);
			}
			saveCsvWin(reconstructedLines, outputCsvFilePath);
			System.out.println("[SUCCESS]");
			
		} catch(Exception e) {
			System.out.println("[ERROR] "+e.getMessage());
		}
	}
	
}