package copies.id;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDDocumentID extends PDDocument {
	// Stocke les pages et leur id
	// Agit comme un pdDocument classique
	// Seulement utilisé sur la partie id
	private List<PDPageID> pdPageIdList;
	// Changer en une liste d'int? dans le même ordre que les pages (peu safe)
	
	public PDDocumentID() {
		super();
		pdPageIdList = new ArrayList<>();
	}
	
	public void addPage(PDPageID page) {
		pdPageIdList.add(page);
		super.addPage(page);
	}
	
	public List<PDPageID> getPdPageIdList(){
		return pdPageIdList;
	}
	
	@Override
	public PDPageID getPage(int index) {
		return pdPageIdList.get(index);
	}
}
