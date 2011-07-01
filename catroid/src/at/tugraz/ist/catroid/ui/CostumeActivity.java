/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class CostumeActivity extends ListActivity {
	private ArrayList<costumeData> costumeData;
	private ArrayList<String> name;
	private ArrayList<String> image;
	private CostumeAdapter c_adapter;
	private Runnable viewCostumes;
	Bitmap bm;
	int column_index;
	Intent intent = null;
	// Declare our Views, so we can access them later
	String filemanagerstring, selectedImagePath, imagePath, costume, lala, costumeImage;
	Cursor cursor;

	private static final int SELECT_IMAGE = 1;

	private void initListeners() {
		Button addnewcostume = (Button) findViewById(R.id.add_costume_button);
		addnewcostume.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costume);

		costumeData = new ArrayList<costumeData>();
		this.c_adapter = new CostumeAdapter(this, R.layout.activity_costumelist, costumeData);
		setListAdapter(this.c_adapter);

		//costumeData c = new costumeData();
		//c.setCostumeName("cat1");
		//c.setCostumeImage(R.drawable.catroid);
		//costumeData.add(c);

		getListView().setTextFilterEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initListeners();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_IMAGE) {
				Uri selectedImageUri = data.getData();

				//OI FILE Manager
				filemanagerstring = selectedImageUri.getPath();

				//MEDIA GALLERY
				selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath == null) {
					Utils.displayErrorMessage(this, getString(R.string.error_load_image));
					return;
				}
				try {
					File outputFile = StorageHandler.getInstance().copyImage(
							ProjectManager.getInstance().getCurrentProject().getName(), selectedImagePath);
					if (outputFile != null) {
						costumeImage = outputFile.getName();
						c_adapter.notifyDataSetChanged();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				imagePath.getBytes();
				costume = imagePath.toString();
				name.add(costume);
				image.add(costumeImage);
				//ProjectManager.getInstance().getCurrentSprite().addCostumeName(costume);
				//ProjectManager.getInstance().getCurrentSprite().addCostumeImage(costumeImage);

				viewCostumes = new Runnable() {
					public void run() {
						getCostumes();
					}
				};

				Thread thread = new Thread(null, viewCostumes, "MagentoBackground");
				thread.start();

			}
		}
	}

	//UPDATED!
	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		imagePath = cursor.getString(column_index);

		return cursor.getString(column_index);
	}

	private Runnable returnRes = new Runnable() {

		public void run() {
			if (costumeData != null && costumeData.size() > 0) {
				c_adapter.notifyDataSetChanged();
				for (int i = 0; i < costumeData.size(); i++) {
					c_adapter.add(costumeData.get(i));
				}
			}
			c_adapter.notifyDataSetChanged();
		}
	};

	public class costumeData {
		private String costumeName;
		@XStreamOmitField
		private transient Bitmap thumbnail;

		public String getCostumeName() {
			return costumeName;
		}

		public void setCostumeName(String costumeName) {
			this.costumeName = costumeName;
		}

		public Bitmap getCostumeImage() {
			return thumbnail;
		}

		public void setCostumeImage(String imageName) {
			if (imageName != null) {
				thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
						Consts.THUMBNAIL_WIDTH);
			}
		}

	}

	private void getCostumes() {
		try {
			costumeData = new ArrayList<costumeData>();
			costumeData c = new costumeData();
			c.setCostumeName(costume);
			c.setCostumeImage(costumeImage);
			costumeData.add(c);

			Log.i("ARRAY", "" + costumeData.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private String getAbsoluteImagePath() {
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.IMAGE_DIRECTORY + "/" + costumeImage;
	}

	private class CostumeAdapter extends ArrayAdapter<costumeData> {

		private ArrayList<costumeData> items;

		public CostumeAdapter(Context context, int textViewResourceId, ArrayList<costumeData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.activity_costumelist, null);
			}

			costumeData c = items.get(position);
			if (c != null) {
				EditText costumeName = (EditText) v.findViewById(R.id.costume_edit_name);
				ImageView costumeImage = (ImageView) v.findViewById(R.id.costume_image);
				if (costumeName != null) {
					costumeName.setText(c.getCostumeName());
				}
				if (costumeImage != null) {
					costumeImage.setImageBitmap(c.getCostumeImage());
				}
			}
			return v;
		}
	}

}
