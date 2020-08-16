package experiment;

import java.util.ArrayList;
import org.junit.runner.JUnitCore;
import IncLing.*;
import IncLingSpecification.SpecificationFeatureAMP4;
import report.Record;
import report.RunListernerReport;
import specifications.Configuration;
import splat.FeatureAMP4Variables;
import testset.*;

public class MainIncLingFeatureAMP4 {

	public void executeJunitTestCase(IncLing incling) {
		int cont = 0;
		Record record = new Record();
		for (Combination combination : incling.getCombsForTest()) {
			for (FeatureIncling f : combination.getListFeatures()) {

				if (f.getName().equals("SKINS")) {
					Configuration.skins = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("PLAYER_CONTROL")) {
					Configuration.player_control = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("REORDER_PLAYLIST")) {
					Configuration.reorder_playlist = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("TITLE_TIME")) {
					Configuration.title_time = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("SKIP_TRACK")) {
					Configuration.skip_track = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("PROGRESS")) {
					Configuration.progress = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("CLEAR_PLAYLIST")) {
					Configuration.clear_playlist = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("PROGRESS_BAR")) {
					Configuration.progress_bar = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("VOLUME_CONTROL")) {
					Configuration.volume_control = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("REMOVE_TRACK")) {
					Configuration.remove_track = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("LIGHT")) {
					Configuration.light = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("DARK")) {
					Configuration.dark = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("SHUFFLE_REPEAT")) {
					Configuration.shuffle_repeat = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("SHOW_COVER")) {
					Configuration.show_cover = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("OGG")) {
					Configuration.ogg = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("MP3")) {
					Configuration.mp3 = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("SAVE_LOAD_PLAYLIST")) {
					Configuration.save_load_playlist = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("BASE_FEATUREAMP")) {
					Configuration.base_featureamp = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("LOAD_FOLDER")) {
					Configuration.load_folder = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("QUEUE_TRACK")) {
					Configuration.queue_track = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("FILE_SUPPORT")) {
					Configuration.file_support = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("PLAYER_BAR")) {
					Configuration.player_bar = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("MUTE")) {
					Configuration.mute = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("ID3_TITLE")) {
					Configuration.id3_title = (f.getState().equals("0") ? false : true);
				}
				if (f.getName().equals("PLAYLIST")) {
					Configuration.playlist = (f.getState().equals("0") ? false : true);
				}
			}
			if (Configuration.validProduct()) {
				cont++;
				Configuration.productPrint();

				/* Chama a blibioteca core do junit para rodar a suite de testes */
				JUnitCore junit = new JUnitCore();
				junit.addListener(new RunListernerReport(Configuration.returnProduct(), record));
				org.junit.runner.Result result = junit.run(AppGUITest.class);
				/* fim core junit */
				System.err.println("cont: " + cont + "((( apos os testes))) ");
				Configuration.productPrint();
				System.out.println("\n\n ----------------------- \n\n");
			} else {
				System.err.println("****** Invalid ******");
			}

		}
		try {
			record.record2();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Contador de produtos:" + cont);

	}

	public void expRun() {
		ArrayList<String> featureName = new ArrayList<String>();
		featureName.add("SKINS");
		featureName.add("PLAYER_CONTROL");
		featureName.add("REORDER_PLAYLIST");
		featureName.add("TITLE_TIME");
		featureName.add("SKIP_TRACK");
		featureName.add("PROGRESS");
	    featureName.add("CLEAR_PLAYLIST");
		featureName.add("PROGRESS_BAR");
		featureName.add("VOLUME_CONTROL");
		featureName.add("REMOVE_TRACK");
		featureName.add("LIGHT");
		featureName.add("DARK");
		featureName.add("SHUFFLE_REPEAT");
		featureName.add("SHOW_COVER");
		featureName.add("OGG");
		featureName.add("MP3");
		featureName.add("SAVE_LOAD_PLAYLIST");
		featureName.add("BASE_FEATUREAMP");
		featureName.add("LOAD_FOLDER");
		featureName.add("QUEUE_TRACK");
		featureName.add("FILE_SUPPORT");
		featureName.add("PLAYER_BAR");
		featureName.add("MUTE");
		featureName.add("ID3_TITLE");
		featureName.add("PLAYLIST");

		IncLing incling = new IncLing(1000, 10000, featureName,
				SpecificationFeatureAMP4.getSINGLETON(Configuration.tool), FeatureAMP4Variables.getSINGLETON());
		executeJunitTestCase(incling);
	}

	public static void main(String[] args) {
		MainIncLingFeatureAMP4 run = new MainIncLingFeatureAMP4();
		run.expRun();

	}
}