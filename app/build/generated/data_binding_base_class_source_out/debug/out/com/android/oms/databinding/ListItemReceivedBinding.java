// Generated by view binder compiler. Do not edit!
package com.android.oms.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.android.oms.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ListItemReceivedBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView tvDateTime;

  @NonNull
  public final TextView tvMessageBody;

  @NonNull
  public final TextView tvSender;

  private ListItemReceivedBinding(@NonNull ConstraintLayout rootView, @NonNull TextView tvDateTime,
      @NonNull TextView tvMessageBody, @NonNull TextView tvSender) {
    this.rootView = rootView;
    this.tvDateTime = tvDateTime;
    this.tvMessageBody = tvMessageBody;
    this.tvSender = tvSender;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ListItemReceivedBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListItemReceivedBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.list_item_received, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListItemReceivedBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.tvDateTime;
      TextView tvDateTime = ViewBindings.findChildViewById(rootView, id);
      if (tvDateTime == null) {
        break missingId;
      }

      id = R.id.tvMessageBody;
      TextView tvMessageBody = ViewBindings.findChildViewById(rootView, id);
      if (tvMessageBody == null) {
        break missingId;
      }

      id = R.id.tvSender;
      TextView tvSender = ViewBindings.findChildViewById(rootView, id);
      if (tvSender == null) {
        break missingId;
      }

      return new ListItemReceivedBinding((ConstraintLayout) rootView, tvDateTime, tvMessageBody,
          tvSender);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
