package generation.pdf;

import org.apache.pdfbox.pdmodel.PDPage;

import config.Config;
import config.Question;
import config.QuestionBoite;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import copies.id.PDDocumentID;


public interface CopiePdf {

	public void generatePDF(Config config) throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException;

	public abstract void generateHeader(Config config);

	public abstract float generateBody(Config config);

	public abstract float generateOpenQ(QuestionBoite q, int qIndex, PDDocumentID pdDocument, PDPage curPage,
			float widthOffset, float heightOffset, int numPage);

	public abstract float generateQCM(Question q, int qIndex, PDDocumentID pdDocument, PDPage curPage, float widthOffset,
			float heightOffset, Config config, int numPage);

	public void generateMarks() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException;

	public void generateRectID();

	public void generateFooter();

	public void generateNumEtudArea(Config config);

	public void generateNameArea();

	public void save(String dest);
}
