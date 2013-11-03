/*
 * Copyright 2011 Azwan Adli Abdullah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gh4a.adapter;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gh4a.Gh4Application;
import com.gh4a.R;
import com.gh4a.utils.GravatarHandler;

public class IssueAdapter extends RootAdapter<Issue> implements OnClickListener {
    public IssueAdapter(Context context) {
        super(context);
    }
    
    @Override
    public View doGetView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (v == null) {
            v = inflater.inflate(R.layout.row_issue, null);

            Gh4Application app = (Gh4Application) mContext.getApplicationContext();
            Typeface boldCondensed = app.boldCondensed;
            Typeface regular = app.regular;
            
            viewHolder = new ViewHolder();
            viewHolder.ivGravatar = (ImageView) v.findViewById(R.id.iv_gravatar);
            viewHolder.ivGravatar.setOnClickListener(this);

            viewHolder.tvDesc = (TextView) v.findViewById(R.id.tv_desc);
            viewHolder.tvDesc.setTypeface(boldCondensed);
            
            viewHolder.tvExtra = (TextView) v.findViewById(R.id.tv_extra);
            viewHolder.tvExtra.setTypeface(regular);
            
            viewHolder.llLabels = (LinearLayout) v.findViewById(R.id.ll_labels);
            viewHolder.tvNumber = (TextView) v.findViewById(R.id.tv_number);
            viewHolder.ivAssignee = (ImageView) v.findViewById(R.id.iv_assignee);
            viewHolder.tvComments = (TextView) v.findViewById(R.id.tv_comments);
            viewHolder.tvMilestone = (TextView) v.findViewById(R.id.tv_milestone);
            
            v.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) v.getTag();
        }

        final Issue issue = mObjects.get(position);

        GravatarHandler.assignGravatar(viewHolder.ivGravatar, issue.getUser());
        viewHolder.ivGravatar.setTag(issue);
        
        viewHolder.tvNumber.setText(String.valueOf(issue.getNumber()));
        viewHolder.llLabels.removeAllViews();

        //show labels
        List<Label> labels = issue.getLabels();
        if (labels != null) {
            for (Label label : labels) {
                TextView tvLabel = (TextView) inflater.inflate(R.layout.issue_list_label,
                        viewHolder.llLabels, false);
                int color = Color.parseColor("#" + label.getColor());
                boolean dark = Color.red(color) + Color.green(color) + Color.blue(color) < 383;

                tvLabel.setText(label.getName());
                tvLabel.setBackgroundColor(color);
                tvLabel.setTextColor(v.getResources().getColor(
                        dark ? android.R.color.primary_text_dark : android.R.color.primary_text_light));
                viewHolder.llLabels.addView(tvLabel);
            }
        }

        viewHolder.tvDesc.setText(issue.getTitle());

        viewHolder.tvExtra.setText(issue.getUser().getLogin() + "\n" + pt.format(issue.getCreatedAt()));
        if (issue.getAssignee() != null) {
            viewHolder.ivAssignee.setVisibility(View.VISIBLE);
            GravatarHandler.assignGravatar(viewHolder.ivAssignee, issue.getAssignee());
        } else {
            viewHolder.ivAssignee.setVisibility(View.GONE);
        }

        viewHolder.tvComments.setText(String.valueOf(issue.getComments()));

        if (issue.getMilestone() != null) {
            viewHolder.tvMilestone.setVisibility(View.VISIBLE);
            viewHolder.tvMilestone.setText(mContext.getString(R.string.issue_milestone,
                    issue.getMilestone().getTitle()));
        }
        else {
            viewHolder.tvMilestone.setVisibility(View.GONE);
        }
        
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_gravatar) {
            Issue issue = (Issue) v.getTag();
            /** Open user activity */
            Gh4Application.get(mContext).openUserInfoActivity(mContext, issue.getUser().getLogin(), null);
        }
    }

    private static class ViewHolder {
        public ImageView ivGravatar;
        public TextView tvDesc;
        public TextView tvExtra;
        public LinearLayout llLabels;
        public TextView tvNumber;
        public ImageView ivAssignee;
        public TextView tvComments;
        public TextView tvMilestone;
    }
}