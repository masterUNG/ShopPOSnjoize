package njoize.dai_ka.com.demotestprint;

public class MyConstant {

    //    Explicit
    private String ipAddressPrinter = "192.168.1.87";
    private int portPrinter = 9100;


    private String urlGetUserWhereName = "http://www.brainwakecafe.com/android/getUserWhereName.php";

    private String nameShopString = "Name Shop";

    private String[] titleTabStrings = new String[]{"Bill", "Desk", "Food", "Noti"};

    private String[] billTitleStrings = new String[]{"ยังไม่ชำระ", "สำเร็จ", "ยกเลิก", "สั่งซื้อแบบออนไลน์"};

    private int[] iconBillTitleInts = new int[]{R.drawable.ic_action_bill, R.drawable.ic_action_desk, R.drawable.ic_action_food, R.drawable.ic_action_noti};

    private String urlTestReadAllData = "https://jsonplaceholder.typicode.com/users";

    private String urlBillWhereOrder = "http://www.brainwakecafe.com/android/getAllOrder.php";

    private String urlBillDetailWhereOID = "http://www.brainwakecafe.com/android/getAllOrderDetail.php";

    private String urlBillFinishWhereOrder = "http://www.brainwakecafe.com/android/getFinishOrder.php";

    private String urlBillCancleWhereOrder = "http://www.brainwakecafe.com/android/getCancleOrder.php";

    public String getUrlBillCancleWhereOrder() {
        return urlBillCancleWhereOrder;
    }

    public String getUrlBillFinishWhereOrder() {
        return urlBillFinishWhereOrder;
    }

    public String getUrlBillDetailWhereOID() {
        return urlBillDetailWhereOID;
    }

    private String sharePreferFile = "userLogin";

    private String urlReadAllDesk = "http://www.brainwakecafe.com/android/getAllDesk.php";


//    Getter


    public String getIpAddressPrinter() {
        return ipAddressPrinter;
    }

    public int getPortPrinter() {
        return portPrinter;
    }

    public String getUrlReadAllDesk() {
        return urlReadAllDesk;
    }

    public String getSharePreferFile() {
        return sharePreferFile;
    }

    public String getUrlBillWhereOrder() {
        return urlBillWhereOrder;
    }

    public String getUrlTestReadAllData() {
        return urlTestReadAllData;
    }

    public int[] getIconBillTitleInts() {
        return iconBillTitleInts;
    }

    public String[] getBillTitleStrings() {
        return billTitleStrings;
    }

    public String[] getTitleTabStrings() {
        return titleTabStrings;
    }

    public String getNameShopString() {
        return nameShopString;
    }

    public String getUrlGetUserWhereName() {
        return urlGetUserWhereName;
    }
}
