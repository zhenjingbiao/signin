package com.example.jingbiaozhen.sign_in.fragment;

/*
 * Created by jingbiaozhen on 2018/5/23.
 * 请假页面
 **/

import java.util.Calendar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jingbiaozhen.sign_in.R;
import com.example.jingbiaozhen.sign_in.bean.BaseLocalModel;
import com.example.jingbiaozhen.sign_in.utils.Constants;
import com.example.jingbiaozhen.sign_in.utils.JsonHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

public class LeaveFragment extends BaseFragment implements DatePicker.OnDateChangedListener
{
    @BindView(R.id.leave_content_et)
    EditText mLeaveContentEt;

    @BindView(R.id.select_leave_tv)
    TextView mSelectLeaveTv;

    @BindView(R.id.leave_time_tv)
    TextView mLeaveTimeTv;

    @BindView(R.id.submit_leave_btn)
    Button mSubmitLeaveBtn;

    private int year, month, day;

    private StringBuilder mDataSb = new StringBuilder();

    @Override
    protected int loadLayout()
    {
        return R.layout.fragment_leave;
    }

    @Override
    protected void initData(Bundle bundle)
    {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void initView()
    {
        super.initView();
    }

    @OnClick({R.id.select_leave_tv, R.id.submit_leave_btn})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.select_leave_tv:
                selectLeaveTime();
                break;
            case R.id.submit_leave_btn:
                submitLeaveInfo();
                break;
            default:
                break;
        }
    }

    /**
     * 提交请假信息
     */
    private void submitLeaveInfo() {
        String leaveInfo=mLeaveContentEt.getText().toString();
        String leaveTime=mLeaveTimeTv.getText().toString();
        String username=mActivity.getSharedPreferences("user", MODE_PRIVATE).getString("username","");
        OkHttpUtils.post().url(Constants.UPLOAD_LEAVE_INFO).addParams("leave_info",leaveInfo)
                .addParams("user_name",username).addParams("leave_time",leaveTime).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mActivity, "请假申请提交失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        BaseLocalModel model= JsonHelper.parseJson(response);
                        if(model.isSucess()){
                            Toast.makeText(mActivity, "请假申请提交成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mActivity, "请假申请提交失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void selectLeaveTime()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final AlertDialog dialog = builder.create();

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (mDataSb.length() > 0)
                { // 清除上次记录的日期
                    mDataSb.delete(0, mDataSb.length());
                }
                mLeaveTimeTv.setText(
                        mDataSb.append(String.valueOf(year)).append("年").append(String.valueOf(month)).append(
                                "月").append(day).append("日"));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialog.dismiss();
            }
        });
        View dialogView = View.inflate(mActivity, R.layout.dialog_date, null);
        final DatePicker datePicker =  dialogView.findViewById(R.id.datePicker);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        // 初始化日期监听事件
        datePicker.init(year, month - 1, day, this);
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}