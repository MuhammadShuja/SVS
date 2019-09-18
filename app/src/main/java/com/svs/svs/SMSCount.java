package com.svs.svs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Muhammad on 19/12/2017.
 */

public class SMSCount extends Dialog implements
        View.OnClickListener {

    private Activity c;
    private Button send, cancel;
    private TextView txtCount;
    private int count = 0;

    public SMSCount(Activity a, int count) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.count = count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove default title from dialog box
        setContentView(R.layout.layout_sms_count);
        send = (Button) findViewById(R.id.btnSend);
        cancel = (Button) findViewById(R.id.btnCancel);
        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
        txtCount = (TextView) findViewById(R.id.txtCount);
        txtCount.setText(count);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                Intent intant = new Intent(c, MainActivity.class);
                getContext().startActivity(intant);
                break;
            case R.id.btnCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
