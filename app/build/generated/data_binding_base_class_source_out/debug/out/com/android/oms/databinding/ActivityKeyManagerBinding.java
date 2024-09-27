// Generated by view binder compiler. Do not edit!
package com.android.oms.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.android.oms.R;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityKeyManagerBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button button2;

  @NonNull
  public final Button button3;

  @NonNull
  public final Button button4;

  @NonNull
  public final Guideline guideline6;

  @NonNull
  public final Guideline guideline7;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final TextInputEditText qrNameView;

  @NonNull
  public final ImageView qrcodeView;

  @NonNull
  public final TextView textView2;

  private ActivityKeyManagerBinding(@NonNull ConstraintLayout rootView, @NonNull Button button2,
      @NonNull Button button3, @NonNull Button button4, @NonNull Guideline guideline6,
      @NonNull Guideline guideline7, @NonNull ConstraintLayout main,
      @NonNull TextInputEditText qrNameView, @NonNull ImageView qrcodeView,
      @NonNull TextView textView2) {
    this.rootView = rootView;
    this.button2 = button2;
    this.button3 = button3;
    this.button4 = button4;
    this.guideline6 = guideline6;
    this.guideline7 = guideline7;
    this.main = main;
    this.qrNameView = qrNameView;
    this.qrcodeView = qrcodeView;
    this.textView2 = textView2;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityKeyManagerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityKeyManagerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_key_manager, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityKeyManagerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button2;
      Button button2 = ViewBindings.findChildViewById(rootView, id);
      if (button2 == null) {
        break missingId;
      }

      id = R.id.button3;
      Button button3 = ViewBindings.findChildViewById(rootView, id);
      if (button3 == null) {
        break missingId;
      }

      id = R.id.button4;
      Button button4 = ViewBindings.findChildViewById(rootView, id);
      if (button4 == null) {
        break missingId;
      }

      id = R.id.guideline6;
      Guideline guideline6 = ViewBindings.findChildViewById(rootView, id);
      if (guideline6 == null) {
        break missingId;
      }

      id = R.id.guideline7;
      Guideline guideline7 = ViewBindings.findChildViewById(rootView, id);
      if (guideline7 == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.qrNameView;
      TextInputEditText qrNameView = ViewBindings.findChildViewById(rootView, id);
      if (qrNameView == null) {
        break missingId;
      }

      id = R.id.qrcodeView;
      ImageView qrcodeView = ViewBindings.findChildViewById(rootView, id);
      if (qrcodeView == null) {
        break missingId;
      }

      id = R.id.textView2;
      TextView textView2 = ViewBindings.findChildViewById(rootView, id);
      if (textView2 == null) {
        break missingId;
      }

      return new ActivityKeyManagerBinding((ConstraintLayout) rootView, button2, button3, button4,
          guideline6, guideline7, main, qrNameView, qrcodeView, textView2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
