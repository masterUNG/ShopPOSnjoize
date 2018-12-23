package njoize.dai_ka.com.demotestprint;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.wfsdk.WifiCommunication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BillDetailFragment extends Fragment {

    //    Explicit
    private WifiCommunication wifiCommunication;
    private boolean aBoolean = false;
    private boolean communicationABoolean = true; // true ==> Can Print, false ==> Disable Print
    private Button button, printAgainButton;
    private int anInt = 0;



    private String idBillString, timeString, cnumString, typeString, nameString, zoneString, deskString;
    private String tag = "2decV2";
    private MyConstant myConstant = new MyConstant();


    public BillDetailFragment() {
        // Required empty public constructor
    }

    public static BillDetailFragment billDetailInstance(String idString,
                                                        String timeString,
                                                        String cnumString,
                                                        String typeString,
                                                        String nameString,
                                                        String zoneString,
                                                        String deskString) {

        BillDetailFragment billDetailFragment = new BillDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("idBill", idString);
        bundle.putString("Time", timeString);
        bundle.putString("cnum", cnumString);
        bundle.putString("type", typeString);
        bundle.putString("name", nameString);
        bundle.putString("Zone", zoneString);
        bundle.putString("Desk", deskString);
        billDetailFragment.setArguments(bundle);
        return billDetailFragment;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Check Connected Printer
        createCommunicationPrinter();

//        Print Controller
        button = getView().findViewById(R.id.btnPayment);
        printAgainButton = getView().findViewById(R.id.btnPaymentAgain);
        printAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCommunicationPrinter();
                communicationABoolean = true;
            }
        });


//        Get OID
        getOID();

//        Create Detail
        createDetail();

//        Show Text
        showText();



    } // Main Method

    private void showText() {
        TextView leftTextView = getView().findViewById(R.id.txtLeft);
        TextView rightTextView = getView().findViewById(R.id.txtRight);

        leftTextView.setText(timeString + " ลูกค้า " + cnumString + " คน " + typeString + " โดย " + nameString);
        rightTextView.setText(zoneString + " " + "โต๊ะ " + deskString);


    }

    private void createDetail() {

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewBillDetail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<String> nameStringArrayList = new ArrayList<>();
        ArrayList<String> detailStringArrayList = new ArrayList<>();
        ArrayList<String> amountStringArrayList = new ArrayList<>();
        ArrayList<String> billStringArrayList = new ArrayList<>();
        ArrayList<String> priceStringArrayList = new ArrayList<>();


        try {

            GetDtailBillWhereID getDtailBillWhereID = new GetDtailBillWhereID(getActivity());
            getDtailBillWhereID.execute(idBillString, myConstant.getUrlBillDetailWhereOID());
            String jsonString = getDtailBillWhereID.get();
            Log.d(tag, jsonString);

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i += 1) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                nameStringArrayList.add(jsonObject.getString("pname"));
                detailStringArrayList.add(jsonObject.getString("des"));
                amountStringArrayList.add("ราคา " + jsonObject.getString("price") + " บาท จำนวน " + jsonObject.getString("num"));
                billStringArrayList.add(jsonObject.getString("setpr"));
                priceStringArrayList.add(jsonObject.getString("sumPrice"));
//                priceStringArrayList.add(jsonObject.getString("price") + ".-");
            }

            BillDetailAdapter billDetailAdapter = new BillDetailAdapter(getActivity(), nameStringArrayList,
                    detailStringArrayList, amountStringArrayList, billStringArrayList, priceStringArrayList);
            recyclerView.setAdapter(billDetailAdapter);


            int total = 0;
            for (String s : priceStringArrayList) {
                total = total + Integer.parseInt(s.trim());
            }

            TextView textView = getView().findViewById(R.id.txtTotal);
            textView.setText("ยอดสุทธิ " + Integer.toString(total) + " บาท");


        } catch (Exception e) {
            //e.printStackTrace();
            Log.d(tag, "e at createDetail ==> " + e.toString());
        }

    }

    private void getOID() {
        idBillString = getArguments().getString("idBill");
        timeString = getArguments().getString("Time");
        cnumString = getArguments().getString("cnum");
        typeString = getArguments().getString("type");
        nameString = getArguments().getString("name");
        zoneString = getArguments().getString("Zone");
        deskString = getArguments().getString("Desk");
        Log.d(tag, "idBill ==> " + idBillString);
    }




    private void createCommunicationPrinter() {
        wifiCommunication = new WifiCommunication(handler);
        wifiCommunication.initSocket( "192.168.1.87", 9100);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String tag = "12decV1";
            switch (msg.what) {

                case WifiCommunication.WFPRINTER_CONNECTED:
                    Log.d(tag, "Success Connected Printer");
                    button.setText("ชำระเงิน");

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (communicationABoolean) {

                                anInt += 1;

                                String printSring = "Print";
                                String line2String = "OK";
                                printSring = printSring + " " + Integer.toString(anInt);
                                Log.d("12decV1", "You Click Payment: " + printSring);


                                byte[] top      = new byte[]{0x10, 0x04, 0x04}; // Space Front Bill
                                //byte[] bytes  = new byte[]{0x1B, 0x4A, 40}; // Space Front Bill n*0.125 mm
                                byte[] lineup   = new byte[]{0x0A, 0x0D}; // Update Line
                                byte[] centered = new byte[]{0x1B, 0x61, 1}; // centered
                                byte[] left     = new byte[]{0x1B, 0x61, 0}; // left
                                byte[] right    = new byte[]{0x1B, 0x61, 2}; // right
                                byte[] tab    = new byte[]{27,101,0,9}; // tab
                                byte[] tab1    = new byte[]{27,101,48,1}; // tab
                                byte[] tab2    = new byte[]{27,101,48,2}; // tab
                                byte[] tab3    = new byte[]{27,101,48,3}; // tab
//                                byte[] tab0    = new byte[]{27,68, 9}; // tab
                                byte[] tab0    = new byte[]{9}; // tab
                                byte[] dfont    = new byte[]{0x1B, 0x21, 0x00}; // default font
                                byte[] bold     =  new  byte [ 3 ]; // Set the font (double height and width bold)
                                bold[0] = 0x1B;
                                bold[1] = 0x21;
                                bold[2] |= 0x04; // 08 04 bold
                                bold[2] |= 0x08; // 10 08 height
                                bold[2] |= 0x20; // 20 10
                                byte[] openCashDrawer   = new byte[]{0x1B, 0x70, 0x00, 0x40, 0x50}; // Open Cash Drawer
                                byte[] cutterPaper      = new byte[]{0x1D, 0x56, 0x42, 90}; // Cutter Paper command


                                byte[] OVERLINE         = new byte[] { 0x1B,0x2D,0x01 };
                                byte[] UNDERLINE        = new byte[] { 0x1B,0x2D,0x01 };
                                byte[] ROW_SPACE        = new byte[] { 0x1B, 0x31, 0x06 }; //
                                byte[] ROW_DEFAULT      = new byte[] { 0x1B, 0x32 }; //
                                byte[] ROW              = new byte[] { 0x1B, 0x33,0x00 }; //
                                byte[] INIT             = new byte[] { 0x1B, 0x40 };
                                byte[] CLEAN            = new byte[] { 0x18 };
                                byte[] LF               = new byte[] { 0x0A };
                                byte[] CR               = new byte[] { 0x0D };
                                byte[] DLE_EOT_1        = new byte[] { 0x10,0x04,0x01 };
                                byte[] DLE_EOT_2        = new byte[] { 0x10,0x04,0x02 };
                                byte[] DLE_EOT_3        = new byte[] { 0x10,0x04,0x03 };
                                byte[] DLE_EOT_4        = new byte[] { 0x10,0x04,0x04 }; //top
                                byte[] DOUBLE_WIDTH     = new byte[] { 0x1B,0x0E };
                                byte[] CANCEL_DOUBLE_WIDTH = new byte[] { 0x1B,0x14 };
                                byte[] BOLD             = new byte[] { 0x1B,0x45,0x01 };
                                byte[] CANCEL_BOLD      = new byte[] { 0x1B,0x45,0x00 };
                                byte[] MOVE_POINT       = new byte[] { 0x1B,0x4A,0x00 }; //ติดบรรทัดบน
                                byte[] FONT             = new byte[] { 0x1B,0x4D,0x00 };
                                byte[] RINGHTMARGIN     = new byte[] { 0x1B,0x51,0x05 };
                                byte[] TRANSVERSE       = new byte[] { 0x1B,0x55,0x03 };
                                byte[] LONGITUDINAL     = new byte[] { 0x1B,0x56,0x01 }; //แนวนอน
                                byte[] ALIGN_LEFT       = new byte[] { 0x1B,0x61,0x00 }; //left
                                byte[] ALIGN_CENTER     = new byte[] { 0x1B,0x61,0x01 }; //centered
                                byte[] ALIGN_RIGHT      = new byte[] { 0x1B,0x61,0x02 }; //right
                                byte[] DIRECTION        = new byte[] { 0x1B,0x63,0x00 }; //กลับไปจัดซ้าย
                                byte[] MOVE_LINE        = new byte[] { 0x1B,0x64,0x08 }; //เว้น8บรรทัด
                                byte[] BLANK_LINE       = new byte[] { 0x1B,0x66,0x00,0x02 }; //
                                byte[] ROTATION         = new byte[] { 0x1B,0x49,0x00 };

                                wifiCommunication.sndByte(openCashDrawer);

//                                wifiCommunication.sndByte(top);
                                wifiCommunication.sndByte(bold);
                                wifiCommunication.sndByte(centered);
                                wifiCommunication.sendMsg("Brainwake", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sendMsg("Matichon Academy", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sendMsg("02 003 4511", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sndByte(dfont);
                                wifiCommunication.sendMsg("-------------------------", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sndByte(left);
                                wifiCommunication.sendMsg("Egg Benedict 1 ฟอง", "tis-620");
                                wifiCommunication.sndByte(tab);
                                wifiCommunication.sendMsg("80", "tis-620");
                                wifiCommunication.sndByte(tab);
                                wifiCommunication.sendMsg("x 2", "tis-620");
                                wifiCommunication.sndByte(tab);
                                wifiCommunication.sendMsg("160", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sndByte(left);
                                wifiCommunication.sendMsg("Pork Kao Lao แห้ง    ", "tis-620");
                                wifiCommunication.sndByte(tab);
                                wifiCommunication.sendMsg("115", "tis-620");
                                wifiCommunication.sndByte(tab);
                                wifiCommunication.sendMsg("x 1", "tis-620");
                                wifiCommunication.sndByte(tab);
                                wifiCommunication.sendMsg("115", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sndByte(right);
                                wifiCommunication.sendMsg("รวมทั้งสิ้น 275 บาท", "tis-620");
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sndByte(lineup);
                                wifiCommunication.sndByte(centered);
                                wifiCommunication.sendMsg("THANK YOU", "tis-620");
                                wifiCommunication.sndByte(lineup);

                                wifiCommunication.sndByte(cutterPaper);





//                                wifiCommunication.sndByte(left);
//                                wifiCommunication.sendMsg("Tab", "tis-620");
//                                wifiCommunication.sndByte(tab1);
//                                wifiCommunication.sndByte(tab0);
//                                wifiCommunication.sendMsg("1", "tis-620");
//                                wifiCommunication.sndByte(tab2);
//                                wifiCommunication.sndByte(tab0);
//                                wifiCommunication.sendMsg("2", "tis-620");
//                                wifiCommunication.sndByte(tab3);
//                                wifiCommunication.sndByte(tab0);
//                                wifiCommunication.sendMsg("3", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(DIRECTION);
//                                wifiCommunication.sendMsg("DIRECTION", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(cutterPaper);

//                                wifiCommunication.sndByte(RINGHTMARGIN);
////                                wifiCommunication.sendMsg("RINGHTMARGIN", "tis-620");
//                                wifiCommunication.sndByte(DIRECTION);
//                                wifiCommunication.sendMsg("DIRECTION", "tis-620");
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sendMsg("222", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(MOVE_LINE2);
//                                wifiCommunication.sendMsg("MOVE_LINE2", "tis-620");
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sendMsg("222", "tis-620");
//                                wifiCommunication.sndByte(LF);
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sendMsg("333", "tis-620");
//                                wifiCommunication.sndByte(ALIGN_RIGHT);
//                                wifiCommunication.sendMsg("ALIGN_RIGHT", "tis-620");
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sendMsg("ALIGN_RIGHT", "tis-620");
//                                wifiCommunication.sndByte(DIRECTION);
//                                wifiCommunication.sendMsg("DIRECTION", "tis-620");
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sendMsg("1", "tis-620");




//                                byte[] INIT = new byte[] { 0x1B, 0x40 };
//                                wifiCommunication.sndByte(INIT);
//                                wifiCommunication.sendMsg("INIT", "tis-620");
//
//                                byte[] CLEAN = new byte[] { 0x18 };
//                                wifiCommunication.sndByte(CLEAN);
//                                wifiCommunication.sendMsg("CLEAN", "tis-620");


//                                byte[] OVERLINE = new byte[] { 0x1B,0x2D,0x01 };
//                                wifiCommunication.sndByte(OVERLINE);
//                                wifiCommunication.sendMsg("OVERLINE", "tis-620");
//
//                                byte[] UNDERLINE = new byte[] { 0x1B,0x2D,0x01 };
//                                wifiCommunication.sndByte(UNDERLINE);
//                                wifiCommunication.sendMsg("UNDERLINE", "tis-620");
//
//                                byte[] ROW_SPACE = new byte[] { 0x1B, 0x31, 0x06 };
//                                wifiCommunication.sndByte(ROW_SPACE);
//                                wifiCommunication.sendMsg("ROW_SPACE", "tis-620");
//
//                                byte[] ROW_DEFAULT = new byte[] { 0x1B, 0x32 };
//                                wifiCommunication.sndByte(ROW_DEFAULT);
//                                wifiCommunication.sendMsg("ROW_DEFAULT", "tis-620");
//
//                                byte[] ROW = new byte[] { 0x1B, 0x33,0x00 };
//                                wifiCommunication.sndByte(ROW);
//                                wifiCommunication.sendMsg("ROW", "tis-620");
//
//                                byte[] LF = new byte[] { 0x0A };
//                                wifiCommunication.sndByte(LF);
//                                wifiCommunication.sendMsg("LF", "tis-620");
//
//                                byte[] CR = new byte[] { 0x0D };
//                                wifiCommunication.sndByte(CR);
//                                wifiCommunication.sendMsg("CR", "tis-620");
//
//                                byte[] DLE_EOT_1 = new byte[] { 0x10,0x04,0x01 };
//                                wifiCommunication.sndByte(DLE_EOT_1);
//                                wifiCommunication.sendMsg("DLE_EOT_1", "tis-620");
//
//                                byte[] DLE_EOT_2 = new byte[] { 0x10,0x04,0x02 };
//                                wifiCommunication.sndByte(DLE_EOT_2);
//                                wifiCommunication.sendMsg("DLE_EOT_2", "tis-620");
//
//                                byte[] DLE_EOT_3 = new byte[] { 0x10,0x04,0x03 };
//                                wifiCommunication.sndByte(DLE_EOT_3);
//                                wifiCommunication.sendMsg("DLE_EOT_3", "tis-620");
//
//                                byte[] DLE_EOT_4 = new byte[] { 0x10,0x04,0x04 };
//                                wifiCommunication.sndByte(DLE_EOT_4);
//                                wifiCommunication.sendMsg("DLE_EOT_4", "tis-620");
//
//                                byte[] DOUBLE_WIDTH = new byte[] { 0x1B,0x0E };
//                                wifiCommunication.sndByte(DOUBLE_WIDTH);
//                                wifiCommunication.sendMsg("DOUBLE_WIDTH", "tis-620");
//
//                                byte[] CANCEL_DOUBLE_WIDTH = new byte[] { 0x1B,0x14 };
//                                wifiCommunication.sndByte(CANCEL_DOUBLE_WIDTH);
//                                wifiCommunication.sendMsg("CANCEL_DOUBLE_WIDTH", "tis-620");
//
//                                byte[] BOLD = new byte[] { 0x1B,0x45,0x01 };
//                                wifiCommunication.sndByte(BOLD);
//                                wifiCommunication.sendMsg("BOLD", "tis-620");
//
//                                byte[] CANCEL_BOLD = new byte[] { 0x1B,0x45,0x00 };
//                                wifiCommunication.sndByte(CANCEL_BOLD);
//                                wifiCommunication.sendMsg("CANCEL_BOLD", "tis-620");
//
//                                byte[] MOVE_POINT = new byte[] { 0x1B,0x4A,0x00 };
//                                wifiCommunication.sndByte(MOVE_POINT);
//                                wifiCommunication.sendMsg("MOVE_POINT", "tis-620");
//
//                                byte[] FONT = new byte[] { 0x1B,0x4D,0x00 };
//                                wifiCommunication.sndByte(FONT);
//                                wifiCommunication.sendMsg("FONT", "tis-620");
//
//                                byte[] RINGHTMARGIN = new byte[] { 0x1B,0x51,0x05 };
//                                wifiCommunication.sndByte(RINGHTMARGIN);
//                                wifiCommunication.sendMsg("RINGHTMARGIN", "tis-620");
//
//                                byte[] TRANSVERSE = new byte[] { 0x1B,0x55,0x03 };
//                                wifiCommunication.sndByte(TRANSVERSE);
//                                wifiCommunication.sendMsg("TRANSVERSE", "tis-620");
//
//                                byte[] LONGITUDINAL = new byte[] { 0x1B,0x56,0x01 };
//                                wifiCommunication.sndByte(LONGITUDINAL);
//                                wifiCommunication.sendMsg("LONGITUDINAL", "tis-620");
//
//                                byte[] ALIGN_LEFT = new byte[] { 0x1B,0x61,0x00 };
//                                wifiCommunication.sndByte(ALIGN_LEFT);
//                                wifiCommunication.sendMsg("ALIGN_LEFT", "tis-620");
//
//                                byte[] ALIGN_CENTER = new byte[] { 0x1B,0x61,0x01 };
//                                wifiCommunication.sndByte(ALIGN_CENTER);
//                                wifiCommunication.sendMsg("ALIGN_CENTER", "tis-620");
//
//                                byte[] ALIGN_RIGHT = new byte[] { 0x1B,0x61,0x00 };
//                                wifiCommunication.sndByte(ALIGN_RIGHT);
//                                wifiCommunication.sendMsg("ALIGN_RIGHT", "tis-620");
//
//                                byte[] DIRECTION = new byte[] { 0x1B,0x63,0x00 };
//                                wifiCommunication.sndByte(DIRECTION);
//                                wifiCommunication.sendMsg("DIRECTION", "tis-620");
//
//                                byte[] MOVE_LINE = new byte[] { 0x1B,0x64,0x08 };
//                                wifiCommunication.sndByte(MOVE_LINE);
//                                wifiCommunication.sendMsg("MOVE_LINE", "tis-620");
//
//                                byte[] BLANK_LINE = new byte[] { 0x1B,0x66,0x00,0x02 };
//                                wifiCommunication.sndByte(BLANK_LINE);
//                                wifiCommunication.sendMsg("BLANK_LINE", "tis-620");
//
//                                byte[] LEFTMARGIN = new byte[] { 0x1B,0x61,0x01 };
//                                wifiCommunication.sndByte(LEFTMARGIN);
//                                wifiCommunication.sendMsg("LEFTMARGIN", "tis-620");
//
//                                byte[] ROTATION = new byte[] { 0x1B,0x49,0x00 };
//                                wifiCommunication.sndByte(ROTATION);
//                                wifiCommunication.sendMsg("ROTATION", "tis-620");



//
////                                wifiCommunication.sndByte(top);
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(bold);
//                                wifiCommunication.sndByte(centered);
//                                wifiCommunication.sendMsg("Brainwake", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//
//                                wifiCommunication.sendMsg("Matichon Academy", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//
//                                wifiCommunication.sendMsg("02 005 0026", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(lineup);
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(left);
//                                wifiCommunication.sendMsg("left 1", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(right);
//                                wifiCommunication.sendMsg("right 1", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(left);
//                                wifiCommunication.sendMsg("left 2", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(right);
//                                wifiCommunication.sendMsg("right 2", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(bold);
//                                wifiCommunication.sndByte(right);
//                                wifiCommunication.sendMsg("SUM 123", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//                                wifiCommunication.sndByte(lineup);
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(bold);
//                                wifiCommunication.sndByte(centered);
//                                wifiCommunication.sendMsg("THANK YOU", "tis-620");
//                                wifiCommunication.sndByte(lineup);
//
//                                wifiCommunication.sndByte(dfont);
//                                wifiCommunication.sndByte(left);
//



                                wifiCommunication.close();

                                communicationABoolean = false;

                            } else {

                                //Log.d("24novV3", "Communication Disible");
                                Toast.makeText(getActivity(), "Disable Printer Please Press Click Again", Toast.LENGTH_SHORT).show();
                            }

                        } // onClick
                    });

                    break;
                case WifiCommunication.WFPRINTER_DISCONNECTED:
                    Log.d(tag, "Disconnected Printer");
                    break;
                default:
                    break;

            } // switch

        } // handleMessage
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_detail, container, false);
    }

}
