/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 * 
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.tutorial.Tutorial;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog extends TextDialog {

	public NewSpriteDialog(ProjectActivity projectActivity) {
		super(projectActivity, projectActivity.getString(R.string.new_sprite_dialog_title), projectActivity
				.getString(R.string.new_sprite_dialog_default_sprite_name));
		initKeyAndClickListener();
	}

	public void handleOkButton() {
		String spriteName = input.getText().toString();

		if (spriteName == null || spriteName.equalsIgnoreCase("")) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_invalid));
			return;
		}

		if (projectManager.spriteExists(spriteName)) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_already_exists));
			return;
		}

		Tutorial tutorial = Tutorial.getInstance(projectActivity.getApplicationContext());
		tutorial.setNotification("DialogDone");

		Sprite sprite = new Sprite(spriteName);
		projectManager.addSprite(sprite);
		((ArrayAdapter<?>) ((ProjectActivity) activity).getListAdapter()).notifyDataSetChanged();

		input.setText(null);
		activity.dismissDialog(ProjectActivity.DIALOG_NEW_SPRITE);
	}

	//<<<<<<< HEAD
	//	private void initKeyListener(AlertDialog.Builder builder) {
	//		builder.setOnCancelListener(new OnCancelListener() {
	//
	//			@Override
	//			public void onCancel(DialogInterface dialog) {
	//				Tutorial tutorial = Tutorial.getInstance(projectActivity.getApplicationContext());
	//				tutorial.rewindStep();
	//				tutorial.rewindStep();
	//				tutorial.setNotification("DialogDone");
	//			}
	//		});
	//
	//		builder.setOnKeyListener(new OnKeyListener() {
	//=======
	private void initKeyAndClickListener() {
		dialog.setOnKeyListener(new OnKeyListener() {
			//>>>>>>> origin/master
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String newSpriteName = (input.getText().toString()).trim();
					if (projectManager.spriteExists(newSpriteName)) {

						Utils.displayErrorMessage(projectActivity,
								projectActivity.getString(R.string.spritename_already_exists));
						Tutorial tutorial = Tutorial.getInstance(projectActivity.getApplicationContext());
						tutorial.setNotification("DialogDone");
						Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_already_exists));

					} else {
						handleOkButton();
						return true;
					}
				}
				return false;
			}
		});

		buttonPositive.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleOkButton();
			}
		});

		buttonNegative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				input.setText(null);
				activity.dismissDialog(ProjectActivity.DIALOG_NEW_SPRITE);
			}
		});
	}

}
