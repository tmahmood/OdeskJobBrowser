package tmn.freelance;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class OdeskJobBrowser extends Activity implements OnClickListener {
	Spinner s1;
	Spinner s2;
	Button b1;
	EditText tv;

	int[] ids = { R.array.i_0, R.array.i_1, R.array.i_2, R.array.i_3,
			R.array.i_4, R.array.i_5, R.array.i_6, R.array.i_7, R.array.i_8 };

	ArrayList<ArrayAdapter<CharSequence>> ads = new ArrayList<ArrayAdapter<CharSequence>>(
			9);

	public ArrayAdapter<CharSequence> getList(long s_id) {
		int id = (int) s_id;
		try {
			return ads.get(id);

		} catch (Exception e) {
			ads.add(id, ArrayAdapter.createFromResource(this, ids[id],
					android.R.layout.simple_spinner_item));
			return ads.get(id);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		for (int i = 0; i < 9; i++) {
			getList(i);
		}

		tv = (EditText) findViewById(R.id.search_query);
		s1 = (Spinner) findViewById(R.id.category);
		s2 = (Spinner) findViewById(R.id.subcategory);
		b1 = (Button) findViewById(R.id.initSearch);
		b1.setOnClickListener(this);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.category_array,
				android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		s1.setAdapter(adapter);
		s1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				ArrayAdapter<CharSequence> t = getList(id);
				t.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				s2.setAdapter(t);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.initSearch:
			searchForJob();
			break;
		// More buttons go here (if any) ...
		}
	}

	private void searchForJob() {

		String category = s1.getSelectedItem().toString();
		String subcategory = s2.getSelectedItem().toString();
		String query = tv.getText().toString();

		Intent i = new Intent(this, SearchJobs.class);
		i.putExtra(SearchJobs.CATEGORY, category);
		i.putExtra(SearchJobs.SUBCATEGORY, subcategory);
		i.putExtra(SearchJobs.SEARCHQUERY, query);

		startActivity(i);
	}
}
