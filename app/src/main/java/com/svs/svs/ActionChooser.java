package com.svs.svs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Muhammad on 18/12/2017.
 */

public class ActionChooser extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Button txt, img;

    public ActionChooser(Activity a) { //constructor
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //to remove default title from dialog box
        setContentView(R.layout.layout_action_chooser);
        txt = (Button) findViewById(R.id.btnText);
        img = (Button) findViewById(R.id.btnImage);
        txt.setOnClickListener(this);
        img.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnText:
                Intent intantText = new Intent(c, ActivityText.class);
                getContext().startActivity(intantText);
                break;
            case R.id.btnImage:
                Intent intantImage = new Intent(c, ActivityImage.class);
                getContext().startActivity(intantImage);
                break;
            default:
                break;
        }
        dismiss();
    }
}
