package com.bitlove.fetlife.view.adapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.util.PictureUtil;
import com.bitlove.fetlife.util.ViewUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class PictureGridAdapter extends BaseAdapter {

    private final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener;
    private final FeedItemResourceHelper feedItemResourceHelper;
    private List<FeedEvent> events;

    private List<Picture> pictures = new ArrayList<>();
    private ArrayList<String> gridLinks = new ArrayList<>();
    private ArrayList<String> displayLinks = new ArrayList<>();

    public PictureGridAdapter(FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
        this(null,onItemClickListener);
    }

    public PictureGridAdapter(FeedItemResourceHelper feedItemResourceHelper, FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.feedItemResourceHelper = feedItemResourceHelper;
    }

    public void setEvents(List<FeedEvent> events) {
        pictures.clear();
        gridLinks.clear();
        displayLinks.clear();
        this.events = events;
        for (FeedEvent event : events) {
            Picture picture = feedItemResourceHelper.getPicture(event);
            pictures.add(picture);
            gridLinks.add(picture != null ? picture.getVariants().getMediumUrl() : null);
            displayLinks.add(picture != null ? picture.getVariants().getHugeUrl() : null);
        }
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures.clear();
        gridLinks.clear();
        displayLinks.clear();
        for (Picture picture : pictures) {
            Picture currentPicture = Picture.loadPicture(picture.getId());
            this.pictures.add(currentPicture != null ? currentPicture : picture);
            gridLinks.add(picture != null ? picture.getThumbUrl() : null);
            displayLinks.add(picture != null ? picture.getDisplayUrl() : null);
        }
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Picture getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Uri pictureUri = gridLinks.get(position) != null ? Uri.parse(gridLinks.get(position)) : null;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflater.inflate(R.layout.listitem_feed_griditem, parent, false);
        simpleDraweeView.setImageURI(pictureUri);
        if (pictureUri == null) {
            simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.dummy_avatar);
        } else {
            simpleDraweeView.getHierarchy().setPlaceholderImage(null);
        }
        if (feedItemResourceHelper == null || feedItemResourceHelper.browseImageOnClick()) {
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
                    PictureUtil.setOverlayContent(overlay, getItem(position), onItemClickListener);

                    new ImageViewer.Builder(v.getContext(), displayLinks).setStartPosition(position).setOverlayView(overlay).setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                        @Override
                        public void onImageChange(int position) {
                            PictureUtil.setOverlayContent(overlay, getItem(position), onItemClickListener);
                        }
                    }).show();
                }
            });
        } else {
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onFeedImageClick(feedItemResourceHelper.getFeedStoryType(),feedItemResourceHelper.getUrl(events.get(position)), events.get(position), feedItemResourceHelper.getTargetMember(events.get(position)));
                }
            });
        }

        return simpleDraweeView;
    }

}

