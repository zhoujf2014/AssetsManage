package com.gtafe.assetsmanage.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.gtafe.assetsmanage.R;
import com.gtafe.assetsmanage.beans.EventBusBean;
import com.gtafe.assetsmanage.beans.GetInstrumentInfo;
import com.gtafe.assetsmanage.rfid.AssetsManageActivity;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by ZhouJF on 2018/1/8.
 */

public class AssertsDetailActivity extends BaseActivity {
    private static final String TAG = "AssertsDetailActivity";
    @BindView(R.id.assetsdetail_instrumentnum)
    TextView mAssetsdetailInstrumentnum;
    @BindView(R.id.assetsdetail_instrumentname)
    TextView mAssetsdetailInstrumentname;
    @BindView(R.id.assetsdetail_instrumenttype)
    TextView mAssetsdetailInstrumenttype;
    @BindView(R.id.assetsdetail_specification)
    TextView mAssetsdetailSpecification;
    @BindView(R.id.assetsdetail_purposename)
    TextView mAssetsdetailPurposename;
    @BindView(R.id.assetsdetail_sourcename)
    TextView mAssetsdetailSourcename;
    @BindView(R.id.assetsdetail_statuscodename)
    TextView mAssetsdetailStatuscodename;
    @BindView(R.id.assetsdetail_managername)
    TextView mAssetsdetailManagername;
    @BindView(R.id.assetsdetail_purchasedate)
    TextView mAssetsdetailPurchasedate;
    @BindView(R.id.assetsdetail_instrumentcategoryname)
    TextView mAssetsdetailInstrumentcategoryname;
    @BindView(R.id.commit)
    Button mCommit;
    @BindView(R.id.assetsdetail_rfid)
    TextView mAssetsdetailRfid;
    private GetInstrumentInfo mGetInstrumentInfo;
    public String mSelectRFID;
    public TextViewAdapter mTextViewAdapter;

    @Override
    protected void init() {
        Intent intent = getIntent();
        intent.getSerializableExtra(AssetsManageActivity.INTERFACE);
        if (intent != null) {
            GetInstrumentInfo getInstrumentInfo = (GetInstrumentInfo) intent.getSerializableExtra(AssetsManageActivity.ASSERTSDETAIL);
            if (getInstrumentInfo != null) {
                mGetInstrumentInfo = getInstrumentInfo;
                GetInstrumentInfo.DataBean data = mGetInstrumentInfo.getData();
                mAssetsdetailInstrumentnum.setText("" + data.getInstrumentNum());
                mAssetsdetailInstrumentname.setText("" + data.getInstrumentName());
                mAssetsdetailInstrumenttype.setText("" + data.getInstrumenttype());
                mAssetsdetailSpecification.setText("" + data.getSpecification());
                mAssetsdetailPurposename.setText("" + data.getPurposeName());
                mAssetsdetailSourcename.setText("" + data.getSourceName());
                mAssetsdetailStatuscodename.setText("" + data.getInstrumenttype());
                mAssetsdetailManagername.setText("" + data.getManagerName());
                mAssetsdetailPurchasedate.setText("" + data.getPurchaseDate());
                mAssetsdetailInstrumentcategoryname.setText("" + data.getInstrumentCategoryName());
                mAssetsdetailInstrumentcategoryname.setText("" + data.getInstrumentCategoryName());
                mAssetsdetailRfid.setText("" + data.getRFID());

            }
        }
    }

    @Override
    protected int setView() {
        return R.layout.activity_assetdetail;
    }

    @Override
    protected void activityEnd() {

    }

    @OnClick({R.id.commit, R.id.assetsdetail_rfid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commit:
                RequestBody body = new FormBody.Builder().add("instNos", "[{ InstNo:\"" + mGetInstrumentInfo.getData().getInstrumentNum() + "\",RFID:\"" + mSelectRFID + "\" }]").build();
                loadDataFromServer("api/Instrument/RelatedInstNoToRFID", body, 2);

                break;
            case R.id.assetsdetail_rfid:

                EventBusBean eventBusBean = new EventBusBean();
                eventBusBean.setType(1);
                eventBusBean.setMessage("开始获取RFID号码");
                EventBus.getDefault().post(eventBusBean);
                break;
        }
    }

    private List<String> mRFIDs = new ArrayList<>();

    @Override
    protected void onEventBusMain(EventBusBean eventBusBean) throws IOException {

        if (eventBusBean.getType() == 3) {
            String message = eventBusBean.getMessage();
            if (mRFIDs.contains(message)) {
                mRFIDs.remove(message);
            }
            mRFIDs.add(0, message);
            if (mTextViewAdapter != null) {
                mTextViewAdapter.notifyDataSetChanged();
            }
        }
        if (eventBusBean.getType() == 5) {
            Log.e(TAG, "M100StartInvTag: " + 5 + " eventBusBeangetMessage()=" + eventBusBean.getMessage());
            View rfidListView = View.inflate(mContext, R.layout.dialog_rfidlist, null);
            ListView dialogLv = (ListView) rfidListView.findViewById(R.id.dialog_lv);
            mTextViewAdapter = new TextViewAdapter();
            dialogLv.setAdapter(mTextViewAdapter);
            dialogLv.setDividerHeight(2);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("请扫描RFID")
                    .setCancelable(false)
                    .setView(rfidListView)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EventBusBean eventBusBean = new EventBusBean();
                            eventBusBean.setType(2);
                            eventBusBean.setMessage(mSelectRFID);
                            EventBus.getDefault().post(eventBusBean);
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.show();
            dialogLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSelectRFID = mRFIDs.get(position);
                    mGetInstrumentInfo.getData().setRFID(mSelectRFID);
                    mAssetsdetailRfid.setText( ""+ mSelectRFID);
                    EventBusBean eventBusBean = new EventBusBean();
                    eventBusBean.setType(2);
                    eventBusBean.setMessage(mSelectRFID);
                    EventBus.getDefault().post(eventBusBean);
                  //  showToast(mSelectRFID);
                    dialog.dismiss();
                }
            });
        }
    }

    class TextViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRFIDs.size();
        }

        @Override
        public Object getItem(int position) {
            return mRFIDs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext, R.layout.item_textview, null);
            TextView viewById = (TextView) view.findViewById(R.id.tv);
            viewById.setText(mRFIDs.get(position));
            return view;
        }
    }

    @Override
    protected void loadDataFromServerSuccessful(String string, int tag) {
        super.loadDataFromServerSuccessful(string, tag);
        if (tag == 2) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("绑定成功，是否退出?")
                    .setCancelable(false)
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.dismiss();

                        }
                    });
            AlertDialog dialog = builder.show();
        }


    }
}
