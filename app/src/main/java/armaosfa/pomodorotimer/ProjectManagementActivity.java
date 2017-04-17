package armaosfa.pomodorotimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Andres on 4/12/17.
 */

public class ProjectManagementActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_management_activity);

        final ListView listview = (ListView) findViewById(R.id.listview);
        String[] values = new String[]{"Blog", "CSC 495", "CSC 444", "CSC 445"};

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, values);
        listview.setAdapter(adapter);
    }
}