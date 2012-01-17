package tmn.freelance;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class JobDetails extends Activity {

	JobObject jobj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobdetails);
		jobj = (JobObject) getIntent().getSerializableExtra("job");

		TextView tv = (TextView) findViewById(R.id.textDetails);
		TextView th = (TextView) findViewById(R.id.jobheading);

		Button applybtn = (Button) findViewById(R.id.apply);
		Button viewbtn = (Button) findViewById(R.id.openpage);
		applybtn.setClickable(true);

		applybtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String url = jobj.getApplyLink();

				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);

			}

		});

		viewbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String url = jobj.getJobPage();

				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		th.setText(jobj.getTitle());
		getBiddingDetails();

		tv.setText(jobj.getDescription());

	}

	private void getBiddingDetails() {
		TableLayout tl = (TableLayout) findViewById(R.id.budget_table);

		tl.addView(getRow("Category", jobj.getCategory()));
		tl.addView(getRow("Subcategory", jobj.getSubCategory()));

		String tmp;
		
		tmp = jobj.getBudget();				
		if (tmp != null) {
			tl.addView(getRow("Budget", tmp));
		}

		
		
		tl.addView(getRow("Type", jobj.getType()));
		tl.addView(getRow("Workload", jobj.getActivity()));
		
		tl.addView(getRow("Time created", jobj.getTimeCreated()));
		tl.addView(getRow("Country", jobj.getCountry()));
		tl.addView(getRow("Timezone", jobj.getTimeZone()));
		
		
		
		tl = (TableLayout) findViewById(R.id.prefs);
		tl.addView(getRow("Requirements", jobj.getRequiredSkills()));

		ArrayList<String> pref = jobj.getPreferences();
		for (String string : pref) {
			String v = jobj.getByKey(string);
			String l = jobj.getLabel(string);			
			tl.addView(getRow(l, v));
		}
	}	

	private TableRow getRow(String label, String val) {
		TableRow tr = new TableRow(this);

		TextView tlbl = new TextView(this);
		TextView tval = new TextView(this);

		tlbl.setPadding(0, 0, 10, 0);
		tlbl.setTypeface(Typeface.DEFAULT_BOLD);
		tlbl.setTextColor(Color.GRAY);
		tlbl.setTextSize(12);
		tlbl.setText(label + ": ");		
	
		
		tval.setTextColor(Color.GRAY);
		tval.setTextSize(12);
		tval.setText(val);
				
		tr.addView(tlbl);
		tr.addView(tval);

		return tr;

	}

}
