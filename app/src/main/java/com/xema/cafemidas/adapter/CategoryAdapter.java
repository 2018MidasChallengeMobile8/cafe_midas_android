package com.xema.cafemidas.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.xema.cafemidas.R;
import com.xema.cafemidas.activity.EditProfileActivity;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.Category;
import com.xema.cafemidas.model.Product;
import com.xema.cafemidas.util.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class CategoryAdapter extends ExpandableRecyclerAdapter<Category, Product, CategoryAdapter.CategoryViewHolder, CategoryAdapter.ProductViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private Context mContext;

    private OnAddProductListener onAddProductListener;

    public interface OnAddProductListener {
        void onAddProduct(Category category);
    }

    public void setOnAddProductListener(OnAddProductListener onAddProductListener) {
        this.onAddProductListener = onAddProductListener;
    }

    private OnDeleteCategoryListener onDeleteCategoryListener;

    public interface OnDeleteCategoryListener {
        void onDeleteCategory(Category category, int parentPosition);
    }

    public void setOnDeleteCategoryListener(OnDeleteCategoryListener onDeleteCategoryListener) {
        this.onDeleteCategoryListener = onDeleteCategoryListener;
    }

    private OnDeleteProductListener onDeleteProductListener;

    public interface OnDeleteProductListener {
        void onDeleteProduct(Product product, int parentPosition, int childPosition);
    }

    public void setOnDeleteProductListener(OnDeleteProductListener onDeleteProductListener) {
        this.onDeleteProductListener = onDeleteProductListener;
    }

    private OnEditProductListener onEditProductListener;

    public interface OnEditProductListener {
        void onEditProduct(Product product, int parentPosition, int childPosition);
    }

    public void setOnEditProductListener(OnEditProductListener onEditProductListener) {
        this.onEditProductListener = onEditProductListener;
    }

    public CategoryAdapter(Context context, @NonNull List<Category> categoryList) {
        super(categoryList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View categoryView = mInflater.inflate(R.layout.item_category, parentViewGroup, false);
        return new CategoryViewHolder(categoryView);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View productView = mInflater.inflate(R.layout.item_product, childViewGroup, false);
        return new ProductViewHolder(productView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int parentPosition, @NonNull Category category) {
        categoryViewHolder.bind(mContext, category, onAddProductListener, onDeleteCategoryListener, parentPosition);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ProductViewHolder productViewHolder, int parentPosition, int childPosition, @NonNull Product product) {
        productViewHolder.llBackground.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(mContext, productViewHolder.llBackground);
            ((Activity) mContext).getMenuInflater().inflate(R.menu.menu_delete_edit, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    if (onDeleteProductListener != null)
                        onDeleteProductListener.onDeleteProduct(product, parentPosition, childPosition);
                } else if (item.getItemId() == R.id.menu_edit) {
                    if (onEditProductListener != null)
                        onEditProductListener.onEditProduct(product, parentPosition, childPosition);
                }
                return false;
            });
            menu.show();
            return false;
        });

        productViewHolder.bind(mContext, product, onDeleteProductListener, parentPosition, childPosition);
    }

    final static class CategoryViewHolder extends ParentViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ll_add_product)
        LinearLayout llAddProduct;
        @BindView(R.id.iv_fold)
        ImageView ivFold;
        @BindView(R.id.ll_background)
        LinearLayout llBackground;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llBackground.setOnClickListener(v -> {
                if (isExpanded()) collapseView();
                else expandView();
            });
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (isExpanded()) ivFold.setRotation(0f);
            else ivFold.setRotation(180f);
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);

            RotateAnimation rotateAnimation = new RotateAnimation(180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            rotateAnimation.setFillAfter(true);
            ivFold.startAnimation(rotateAnimation);
        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return false;
        }

        void bind(Context context, Category category, OnAddProductListener onAddProductListener, OnDeleteCategoryListener onDeleteCategoryListener, int parentPosition) {
            tvName.setText(category.getName());
            llAddProduct.setOnClickListener(v -> {
                if (onAddProductListener != null) onAddProductListener.onAddProduct(category);
            });
            llBackground.setOnLongClickListener(v -> {
                if (onDeleteCategoryListener != null)
                    onDeleteCategoryListener.onDeleteCategory(category, parentPosition);
                return false;
            });
        }
    }

    final static class ProductViewHolder extends ChildViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.ll_background)
        LinearLayout llBackground;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Context context, Product product, OnDeleteProductListener onDeleteProductListener, int parentPosition, int childPosition) {
            tvName.setText(product.getName());
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(product.getPrice())));
        }
    }
}