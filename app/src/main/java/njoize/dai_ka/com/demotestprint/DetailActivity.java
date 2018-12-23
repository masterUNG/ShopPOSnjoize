package njoize.dai_ka.com.demotestprint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

//        Get Value
        MyConstant myConstant = new MyConstant();
        String[] strings = myConstant.getDetailStrings();
        String[] valueStrings1 = new String[strings.length];

        for (int i = 0; i < strings.length; i += 1) {
            valueStrings1[i] = getIntent().getStringExtra(strings[i]);
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentDetailFragment, BillDetailFragment.billDetailInstance(
                            valueStrings1[0],
                            valueStrings1[1],
                            valueStrings1[2],
                            valueStrings1[3],
                            valueStrings1[4],
                            valueStrings1[5],
                            valueStrings1[6])).commit();
        }


    }
}
