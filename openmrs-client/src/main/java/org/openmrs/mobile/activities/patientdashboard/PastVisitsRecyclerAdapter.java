package org.openmrs.mobile.activities.patientdashboard;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.List;

public class PastVisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final int VIEW_TYPE_ITEM = 0;
	private final int VIEW_TYPE_LOADING = 1;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean isLoading;
	private Context context;
	private List<Visit> visits;
	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private ObsDataService observationDataService;

	public PastVisitsRecyclerAdapter(RecyclerView recyclerView, List<Visit> visits, Context context) {
		this.visits = visits;
		this.context = context;
		observationDataService = new ObsDataService();
		LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				/*totalItemCount = layoutManager.getItemCount();
				lastVisibleItem = layoutManager.findLastVisibleItemPosition();
				if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
					if (onLoadMoreListener != null) {
						onLoadMoreListener.onLoadMore();
					}
					isLoading = true;
				}*/
				if (!isLoading && onLoadMoreListener != null) {
					isLoading = true;
					onLoadMoreListener.onLoadMore();
				}
			}
		});
	}

	public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
		this.onLoadMoreListener = mOnLoadMoreListener;
	}

	@Override
	public int getItemViewType(int position) {
		return visits.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_ITEM) {
			View view = LayoutInflater.from(context).inflate(R.layout.past_visits_row, parent, false);
			return new VisitViewHolder(view);
		} else if (viewType == VIEW_TYPE_LOADING) {
			View view = LayoutInflater.from(context).inflate(R.layout.past_visits_loading, parent, false);
			return new LoadingViewHolder(view);
		}
		return null;
	}

	public void expandCollapse(View view) {
		if (view.getVisibility() == View.GONE) {
			view.setVisibility(View.VISIBLE);
			//int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			//ConsoleLogger.dump(heightMeasureSpec);
			/*
			final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			view.measure(widthSpec, heightSpec);
			ValueAnimator mAnimator = slideAnimator(view, 0, view.getMeasuredHeight());
			mAnimator.start();*/
		} else {
			view.setVisibility(View.VISIBLE);
			/*int finalHeight = view.getHeight();
			ValueAnimator mAnimator = slideAnimator(view, finalHeight, 0);
			mAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					view.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

			});
			mAnimator.start();*/
		}

	}

	private ValueAnimator slideAnimator(View view, int start, int end) {
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (Integer)valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
				layoutParams.height = value;
				view.setLayoutParams(layoutParams);
			}
		});
		return animator;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof VisitViewHolder) {
			Visit visit = visits.get(position);
			VisitViewHolder viewHolder = (VisitViewHolder)holder;
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View singleVisitView = layoutInflater.inflate(R.layout.past_visits_card, null);
			LinearLayout observationsContainer = (LinearLayout)singleVisitView.findViewById(R.id.observationsContainer);
			TextView visitTitle = (TextView)singleVisitView.findViewById(R.id.visitTitle);
			visitTitle.setText("Visit: " + DateUtils
					.convertTime1(visit.getStopDatetime(), DateUtils
							.PATIENT_DASHBOARD_VISIT_DATE_FORMAT) + " - " + DateUtils
					.convertTime1(visit.getStartDatetime(), DateUtils
							.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			visitTitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					expandCollapse(observationsContainer);
				}
			});
			for (Encounter encounter : visit.getEncounters()) {
				switch (encounter.getEncounterType().getDisplay()) {
					case ApplicationConstants.EncounterTypeEntitys.VISIT_NOTE:
						QueryOptions options = new QueryOptions();
						PagingInfo pagininfo = new PagingInfo(1, 100);//TODO set this to null after Wes has fixed his issues
						observationDataService.getByEncounter(encounter, options, pagininfo,
								new DataService.GetCallback<List<Observation>>() {
									@Override
									public void onCompleted(List<Observation> observations) {
										for (Observation observation : observations) {
											if (observation.getDiagnosisNote() != null && !observation.getDiagnosisNote()
													.equals(ApplicationConstants.EMPTY_STRING)) {
												View row = layoutInflater
														.inflate(R.layout.visits_obervation_row, null);
												((TextView)row.findViewById(R.id.text))
														.setText(observation.getDiagnosisNote());
												observationsContainer.addView(row);
											}
										}
									}

									@Override
									public void onError(Throwable t) {

									}
								});
						break;
				}
			}
			viewHolder.observationsContainer.addView(singleVisitView);

		} else if (holder instanceof LoadingViewHolder) {
			LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
			loadingViewHolder.progressBar.setIndeterminate(true);
		}
	}

	@Override
	public int getItemCount() {
		return visits == null ? 0 : visits.size();
	}

	public void setLoaded() {
		isLoading = false;
	}

	private class LoadingViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public LoadingViewHolder(View view) {
			super(view);
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
		}
	}

	private class VisitViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout observationsContainer;

		public VisitViewHolder(View view) {
			super(view);
			observationsContainer = (LinearLayout)view.findViewById(R.id.observationsContainer);
		}
	}
}