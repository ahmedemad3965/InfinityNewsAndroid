package com.bitnews.bitnews.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationRecyclerAdapter<T> extends RecyclerView.Adapter {

    static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY_ITEM = 1;
    private static final int VIEW_TYPE_LOADING_BAR = 2;
    static int ITEM_VIEW_HEIGHT = 0;

    ArrayList<T> itemsList = new ArrayList<>();
    Context context;
    private RecyclerView recyclerView;
    private boolean isLoadingMore;
    private boolean isLoadingInitially;

    PaginationRecyclerAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM)
            return createItemViewHolder(parent);
        if (viewType == VIEW_TYPE_LOADING_BAR)
            return new RecyclerView.ViewHolder(new ProgressBar(parent.getContext())) {
            };
        return createEmptyItemViewHolder(parent);
    }

    @Override
    public int getItemCount() {
        return itemsList.size() != 0 || !isLoadingInitially ? itemsList.size() : calculateEmptyItemsCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsList.size() == 0)
            return VIEW_TYPE_EMPTY_ITEM;
        if ((position == itemsList.size() - 1 && isLoadingMore) || itemsList.get(position) == null)
            return VIEW_TYPE_LOADING_BAR;
        return VIEW_TYPE_ITEM;
    }

    int calculateEmptyItemsCount() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float parentHeight = recyclerView.getHeight() / displayMetrics.density;

        return (int) (parentHeight / ITEM_VIEW_HEIGHT);
    }

    public void addAll(List<T> objects) {
        if (itemsList.size() != 0) {
            itemsList.addAll(objects);
            notifyItemRangeInserted(itemsList.size() - objects.size(),
                    objects.size());
        } else {
            itemsList.addAll(objects);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        itemsList.clear();
        notifyDataSetChanged();
    }

    public ArrayList<T> getItemsList() {
        return itemsList;
    }

    private void addLoadingBar() {
        itemsList.add(null);
        notifyItemInserted(itemsList.size() - 1);
    }

    private void removeLoadingBar() {
        itemsList.remove(null);
        notifyItemRemoved(itemsList.size() - 1);
    }

    public boolean isLoading() {
        return isLoadingInitially || isLoadingMore;
    }

    public void setLoadingInitially(boolean loading) {
        if (loading) {
            isLoadingInitially = true;
            notifyDataSetChanged();
        } else {
            isLoadingInitially = false;
        }
    }

    public void setLoadingMore(boolean loading) {
        if (loading) {
            isLoadingMore = true;
            addLoadingBar();
        } else {
            isLoadingMore = false;
            removeLoadingBar();
        }
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createEmptyItemViewHolder(ViewGroup parent);
}
