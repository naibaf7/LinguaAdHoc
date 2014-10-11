package ch.smartapes.linguaadhoc.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MultiSelectorDialog {

	private ArrayList selectedItems;

	private AlertDialog.Builder alertDialogBuilder;

	public MultiSelectorDialog(String title, String[] values, Context context,
			ArrayList selected) {
		selectedItems = new ArrayList();
		selectedItems.addAll(selected);
		boolean checked[] = new boolean[values.length];
		for (int i = 0; i < selected.size(); i++) {
			checked[(Integer) selected.get(i)] = true;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMultiChoiceItems(values, null,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							selectedItems.add(which);
						} else if (selectedItems.contains(which)) {
							selectedItems.remove(Integer.valueOf(which));
						}
					}
				});
		alertDialogBuilder = builder;
	}

	public ArrayList getSelectedItems() {
		return selectedItems;
	}

	public AlertDialog.Builder getDialogBuilder() {
		return alertDialogBuilder;
	}

}
