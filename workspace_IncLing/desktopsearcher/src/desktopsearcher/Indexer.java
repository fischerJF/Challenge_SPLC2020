package desktopsearcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import specifications.Configuration;


/**
 * Hilfsklasse fuer den Lucene-Index.
 * 
 * Diese Klasse bietet Zugriffsmethoden auf einen Lucene-Index und kann diesen
 * auch anhand eines Quellverzeichnisses mit beliebigen Dateien selbst neu
 * erzeugen.
 * <p>
 * Es wird empfohlen, den Index nicht in das Quellverzeichnis zu legen, um die
 * Uebersichtlichkeit zu wahren.
 * 
 * @author Mr. Pink.
 */
public class Indexer {
	protected FSDirectory luceneIndex = null; // /< das Verzeichnis, in dem der
												// Index liegt / liegen soll
	protected String luceneDirectory = ""; // /< das Verzeichnis, in dem der
											// Index liegt / liegen soll

	protected ArrayList contentHandlers = null; // /< Liste aller verfuegbaren
												// Content Handler

	protected String[] rootDirectories;

	protected ArrayList alreadyIndexedDirectories;

	/**
	 * Konstruktor.
	 * 
	 * Initialisiert den Indexer in einem Verzeichnis. Sollte das Verzeichnis
	 * nicht existieren, wird eine Exception geworfen. Beim Initialisieren
	 * werden auch das erste (und einzige mal fuer diese Instanz) die Content
	 * Handler ermittelt.
	 * 
	 * @throws IndexerException
	 *             wenn der Ordner nicht existiert
	 * @param luceneDir
	 *            Verzeichnis in dem der Index abgelegt wird
	 */
	public Indexer(String luceneDir) throws IndexerException {
		luceneDirectory = luceneDir;
		System.err.println(luceneDirectory);
		try {
			luceneIndex = FSDirectory.open(new File(luceneDirectory));
		} catch (IOException e) {
			throw new IndexerException("Could not open index.");
		}

		getAvailableContentHandlers();
	}

	/**
	 * Schliesst den Index.
	 */
	public void finalize() {
		luceneIndex.close();
	}

	/**
	 * Schliesst den Indexer.
	 */
	public void close() {
		luceneIndex.close();
	}

	/**
	 * Content Handler laden.
	 * 
	 * Laedt alle Handlerklassen
	 * 
	 */
	public void getAvailableContentHandlers() throws IndexerException {
		contentHandlers = new ArrayList();
		if (Configuration.HTML) {
			contentHandlers.add(new HTML());
		}
		if (Configuration.LATEX) {
			contentHandlers.add(new Latex());
		}
		if (Configuration.TXT) {
			contentHandlers.add(new Plain());
		}
	}

	/**
	 * ContentHandler ermitteln.
	 * 
	 * Sucht nach dem fuer die Datei passenden ContentHandler und gibt ihn
	 * zurueck. Wuerden mehrere ContentHandler passen, so gibt er den zuerst
	 * gefundenen zurueck.
	 * 
	 * @param filename
	 *            der Name der Datei (ohne Pfad)
	 * @return passender ContentHandler oder null, wenn keiner gefunden wurde
	 */
	protected ContentHandler getClassForFilename(String filename) {
		Iterator it = contentHandlers.iterator();
		while (it.hasNext()) {
			ContentHandler c = (ContentHandler) it.next();
			try {
				boolean wantsToHandle = c.handles(filename);
				if (wantsToHandle) {
					return c;
				}
			} catch (Exception e) {
				// pass...
			}
		}

		return null;
	}

	/**
	 * Suche durchfuehren.
	 * 
	 * Diese Methode fuehrt eine Suchanfrage ueber (falls nicht anders im Query
	 * angegeben) alle vorhandenen Felder im Index durch und liefert die
	 * maxDocuments besten Treffer zurueck.
	 * 
	 * @param query
	 *            die Suchanfrage
	 * @param maxDocuments
	 *            maximale Trefferanzahl
	 * @return IDs der besten Treffer oder null, wenn ein Fehler auftrat
	 */
	public TopDocs search(String query, int maxDocuments) {
		// Query parsen und ausfuehren

		String[] fields = getAllIndexedFields();
		QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29,
				fields, new SimpleAnalyzer());
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);

		try {
			return performQuery(parser.parse(query), null, maxDocuments);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Ermittelt aus der Suchanfrage die enthaltenen Suchterme.
	 * 
	 * @param query
	 *            die vom Benutzer angegeben wurde
	 * @return einzelne eindeutige Suchterme
	 */
	public String[] getQueryTerms(String query) {

		String[] fields = getAllIndexedFields();

		QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29,
				fields, new SimpleAnalyzer());

		HashSet set = null;
		try {
			Query q = parser.parse(query);
			IndexReader reader = IndexReader.open(luceneIndex, true);
			q = q.rewrite(reader);

			set = new HashSet();
			q.extractTerms(set);

		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList queryTerms = new ArrayList();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Term t = (Term) it.next();
			if (!queryTerms.contains(t.text())) {
				queryTerms.add(t.text());
			}
		}

		String[] newAr = new String[queryTerms.size()];
		return (String[]) queryTerms.toArray(newAr);
	}

	/**
	 * Gibt die zuletzt veraenderten Dokumente zurueck.
	 * 
	 * @param maxDocuments
	 *            maximale Trefferanzahl
	 * @return IDs der neuesten Dokumente
	 */
	public TopDocs getMostRecentDocuments(int maxDocuments) {
		Sort sorter = new Sort(
				new SortField("lastModify", SortField.LONG, true));
		return performQuery(new MatchAllDocsQuery(), sorter, maxDocuments);
	}

	/**
	 * Gibt die groessten Dokumente zurueck.
	 * 
	 * @param maxDocuments
	 *            maximale Trefferanzahl
	 * @return IDs der groessten Dokumente
	 */
	public TopDocs getLargestDocuments(int maxDocuments) {
		Sort sorter = new Sort(new SortField("size", SortField.LONG, true));
		return performQuery(new MatchAllDocsQuery(), sorter, maxDocuments);
	}

	/**
	 * Durchfuehren der Suchanfrage.
	 * 
	 * Diese Methode fuehrt schlussendlich die eigentliche Suchanfrage durch.
	 * Dafuer verwendet sie die von den public Methoden erzeugten Queries, um
	 * nach Dokumenten zu suchen.
	 * 
	 * @param query
	 *            die zu benutztende Query
	 * @param sorter
	 *            die Sortierparameter
	 * @param max
	 *            maximale Trefferanzahl
	 * @return IDs der gefundenen Dokumente oder null, falls ein Fehler auftrat
	 */
	protected TopDocs performQuery(Query query, Sort sorter, int max) {
		try {

			IndexSearcher searcher = new IndexSearcher(luceneIndex, true);
			TopDocs topDocs = null;

			// Wenn kein Sorter gewuenscht ist, darf auch keiner
			// uebergeben werden (nicht einmal null).

			if (sorter == null) {
				topDocs = searcher.search(query, null, max);
			} else {
				topDocs = searcher.search(query, null, max, sorter);
			}

			searcher.close();
			return topDocs;
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	/**
	 * Gibt alle indexierten Felder zurueck.
	 * 
	 * Diese Methode fragt jeden einzelnen ContentHandler nach den von ihm
	 * erstellten Feldern fuer seine Dokumente, um daraus die Menge aller
	 * verfuegbaren Felder zu ermitteln. In dieser werden dann Suchanfragen
	 * ausgefuehrt. Auf diese Weise muss bei der Suche nicht explizit das zu
	 * verwendende Feld angegeben werden und die Daten neuer ContentHandler sind
	 * sofort verfuegbar.
	 * 
	 * @return die Namen der Felder (ohne Duplikate, unsortiert)
	 */
	private String[] getAllIndexedFields() {
		ArrayList fields = new ArrayList(0);

		Iterator it = contentHandlers.iterator();
		while (it.hasNext()) {
			ContentHandler c = (ContentHandler) it.next();
			try {
				String[] indexedFields = c.getIndexedFields();

				for (int f = 0; f < indexedFields.length; f++) {
					String field = indexedFields[f];
					if (!fields.contains(field)) {
						fields.add(field);
					}
				}
			} catch (Exception e) {
				// pass...
			}
		}

		String[] result = new String[fields.size()];
		fields.toArray(result);
		return result;
	}

	/**
	 * Gibt ein Object[] zurueck.
	 * 
	 * [0] beinhaltet das zu der ID passende Dokument zurueck. [1] beinhaltet
	 * das TermFreqVector[] in dem die Vorkommen der einzelnen Terme des
	 * Dokuments gespeichert sind.
	 * 
	 * @param documentID
	 *            ID des Dokumentes
	 * @return das passenden Dokument und deren TermFreqVecor (siehe Oben) oder
	 *         null, falls kein Dokument mit der ID gefunden wurde
	 */
	public Object[] getDocument(int documentID) {
		try {
			IndexReader reader = IndexReader.open(luceneIndex, true);
			TermFreqVector[] vec = reader.getTermFreqVectors(documentID);
			Object[] ret = new Object[2];
			Document document = reader.document(documentID);

			reader.close();
			ret[0] = document;
			ret[1] = vec;
			return ret;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gibt zurueck, ob schon ein Index fuer den Ordner existiert.
	 * 
	 * @return Index existiert schon?
	 */
	public boolean exists() {
		try {
			return IndexReader.indexExists(luceneIndex);
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Haeufigkeit eines Termes berechnen.
	 * 
	 * Diese Methode berechnet aus dem Uebergebenen TermFreqVector die Anzahl
	 * der Vorkommen des gesuchten Terms und gibt diesen zurueck. Hierbei wird
	 * die Summe aller Vorkommen in allen Fields (content,titel,...) gebildet.
	 * 
	 * @param freqVec
	 *            TermFreqVector eines Dokuments, welches analysiert werden soll
	 * @param queryTerm
	 *            der Suchterm der in dem TermFreqVector (und somit im Dokument)
	 *            gesucht werden soll
	 * @return die Anzahl der Vorkommen des queryTerms in dem TermFreqVector
	 */
	public int getTermFreq(TermFreqVector[] freqVec, String queryTerm) {
		int count = 0;
		for (int i = 0; i < freqVec.length; i++) {
			TermFreqVector vect = freqVec[i];
			for (int m = 0; m < vect.getTerms().length; m++) {
				if (vect.getTerms()[m].compareTo(queryTerm) == 0) {
					count += vect.getTermFrequencies()[m];
					break;
				}
			}
		}
		return count;
	}

	protected String dataDirectory; // /< das Verzeichnis, in dem die Quelldaten
									// liegen

	/**
	 * Index erzeugen.
	 * 
	 * Schreibt den Index fuer das gegebene Verzeichnis. Dabei werden alle
	 * Dateien in allen (Unter-)verzeichnissen betrachtet und falls ein Content
	 * Handler fuer sie verfuegbar ist, auch indexiert.
	 * 
	 * @throws IndexerException
	 *             das angegebene Datenverzeichnis ist ungueltig
	 * @param dataDir
	 *            der absolute Pfad des Verzeichnisses
	 * @return True, wenn der Index erzeugt wurde, ansonsten False
	 */
	public void createIndex(String dataDir) throws Exception {
		if (Configuration.COMMAND_LINE) {

			// Verzeichnis testen
			dataDirectory = dataDir;
			File testDir = new File(dataDirectory);

			if (!testDir.isDirectory() || !testDir.exists()) {
				throw new IndexerException("The data directory does not exist.");
			}

			// Als naechstes holen wir uns einen IndexWriter von Lucene

			IndexWriter writer = new IndexWriter(luceneIndex,
					new SimpleAnalyzer(), true,
					IndexWriter.MaxFieldLength.UNLIMITED);

			// Und auf geht die wilde Reise!

			indexDirectory(dataDir, writer);

			// Index schreiben

			writer.commit();
			writer.close();

			// Kurze Statistik am Ende ausgeben

			IndexReader reader = IndexReader.open(luceneIndex, true);
			System.out.println(reader.numDocs()
					+ " documents have been indexed.");
			reader.close();
		} else if (Configuration.GUI) {
			if (Configuration.SINGLE_DIRECTORY) {
				// Verzeichnis testen

				dataDirectory = dataDir;
				File testDir = new File(dataDirectory);

				if (!testDir.isDirectory() || !testDir.exists()) {
					throw new IndexerException(
							"The data directory does not exist.");
				}

				// Als naechstes holen wir uns einen IndexWriter von Lucene

				IndexWriter writer = new IndexWriter(luceneIndex,
						new SimpleAnalyzer(), true,
						IndexWriter.MaxFieldLength.UNLIMITED);

				// Und auf geht die wilde Reise!

				indexDirectory(dataDir, writer);

				// Index schreiben

				writer.commit();
				writer.close();

				// Kurze Statistik am Ende ausgeben

				IndexReader reader = IndexReader.open(luceneIndex, true);
				JOptionPane
						.showMessageDialog(
								null,
								(Object) (reader.numDocs() + " documents have been indexed."),
								"Done indexing.",
								JOptionPane.INFORMATION_MESSAGE);
				reader.close();
			}
		}
	}

	/**
	 * Index erzeugen.
	 * 
	 * Schreibt den Index fuer das gegebene Verzeichnis. Dabei werden alle
	 * Dateien in allen (Unter-)verzeichnissen betrachtet und falls ein Content
	 * Handler fuer sie verfuegbar ist, auch indexiert.
	 * 
	 * @throws IndexerException
	 *             das angegebene Datenverzeichnis ist ungueltig
	 * @param dataDir
	 *            der absolute Pfad des Verzeichnisses
	 * @return True, wenn der Index erzeugt wurde, ansonsten False
	 */
	public void createIndex(String[] dataDir) throws Exception {
		if (Configuration.GUI) {
			if (Configuration.MULTI_DIRECTORY) {
				// Verzeichnis testen
				rootDirectories = dataDir;
				alreadyIndexedDirectories = new ArrayList();

				// Als naechstes holen wir uns einen IndexWriter von Lucene
				IndexWriter writer = new IndexWriter(luceneIndex,
						new SimpleAnalyzer(), true,
						IndexWriter.MaxFieldLength.UNLIMITED);
				for (int i = 0; i < dataDir.length; i++) {
					File testDir = new File(rootDirectories[i]);
					if (!testDir.isDirectory() || !testDir.exists()) {
						throw new IndexerException(
								"The data directory does not exist: ");
					}
					// Und auf geht die wilde Reise!
					indexDirectory(rootDirectories[i], writer);
					// Index commiten
					writer.commit();
				}
				writer.close();
				// Kurze Statistik am Ende ausgeben
				IndexReader reader = IndexReader.open(luceneIndex, true);
				JOptionPane
						.showMessageDialog(
								null,
								(Object) (reader.numDocs() + " documents have been indexed."),
								"Done indexing.",
								JOptionPane.INFORMATION_MESSAGE);
				reader.close();
			}
		}
	}

	/**
	 * Verzeichnis scannen und indexieren.
	 * 
	 * Jede in dem Verzeichnis gefundene Datei wird auf den passenden
	 * ContentHandler vermittelt. Nur der erste Content Handler, der sie
	 * behandeln will, kommt dabei zum Zuge. Sollte kein CH gefunden werden,
	 * wird die Datei still ignoriert.
	 * 
	 * @param sourceDir
	 *            das zu indexierende Verzeichnis
	 * @param writer
	 *            der Dateiindizierer
	 */
	protected void indexDirectory(String sourceDir, IndexWriter writer) {
		if (Configuration.COMMAND_LINE) {

			File dir = new File(sourceDir);

			// Fehlerhafter Rekursionsaufruf?

			if (!dir.exists() || !dir.isDirectory()) {
				return;
			}

			// Alle Dateien durchwandern...
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					indexDirectory(file.getAbsolutePath(), writer);
				} else {
					String filename = file.getAbsolutePath();
					String basename = file.getName();

					ContentHandler contentHandler = getClassForFilename(basename);

					// Wenn ein Handler gefunden wurde, darf dieser die Datei
					// jetzt indexieren.

					if (contentHandler != null) {

						try {
							// index(String, IndexWriter)-Methode ermitteln und
							// diese fuer die
							// aktuelle Datei aufrufen. Dort findet die
							// eigentliche Indexierung
							// statt.

							// Method index = contentHandler.getMethod("index",
							// String.class, IndexWriter.class);
							boolean ok = contentHandler.index(filename, writer);

							System.out.println(ok ? "ok." : "failed.");
						} catch (Exception e) {
							System.out.println("failed.");
						}
					}
				}
			}
		} else if (Configuration.GUI) {
			if (Configuration.SINGLE_DIRECTORY) {
				File dir = new File(sourceDir);

				// Fehlerhafter Rekursionsaufruf?

				if (!dir.exists() || !dir.isDirectory()) {
					return;
				}

				// Alle Dateien durchwandern...
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.isDirectory()) {
						indexDirectory(file.getAbsolutePath(), writer);
					} else {
						String filename = file.getAbsolutePath();
						String basename = file.getName();

						ContentHandler contentHandler = getClassForFilename(basename);

						// Wenn ein Handler gefunden wurde, darf dieser die
						// Datei
						// jetzt indexieren.

						if (contentHandler != null) {

							try {
								// index(String, IndexWriter)-Methode ermitteln
								// und
								// diese fuer die
								// aktuelle Datei aufrufen. Dort findet die
								// eigentliche Indexierung
								// statt.

								// Method index =
								// contentHandler.getMethod("index",
								// String.class, IndexWriter.class);
								boolean ok = contentHandler.index(filename,
										writer);

								System.out.println(ok ? "ok." : "failed.");
							} catch (Exception e) {
								System.out.println("failed.");
							}
						}
					}
				}
			} else if (Configuration.MULTI_DIRECTORY) {
				File dir = new File(sourceDir);
				// Fehlerhafter Rekursionsaufruf?
				if (!dir.exists() || !dir.isDirectory()) {
					return;
				}
				// mehrfaches Indexieren verhindern
				if (alreadyIndexedDirectories.contains(dir)) {
					return;
				}
				alreadyIndexedDirectories.add(dir);

				// Alle Dateien durchwandern...
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.isDirectory()) {
						indexDirectory(file.getAbsolutePath(), writer);
					} else {
						String filename = file.getAbsolutePath();
						String basename = file.getName();
						ContentHandler contentHandler = getClassForFilename(basename);
						if (contentHandler != null) {
							try {
								boolean ok = contentHandler.index(filename,
										writer);
								System.out.println(ok ? "ok." : "failed.");
							} catch (Exception e) {
								System.out.println("failed.");
							}
						}
					}
				}
			}
		}
	}
	
}
