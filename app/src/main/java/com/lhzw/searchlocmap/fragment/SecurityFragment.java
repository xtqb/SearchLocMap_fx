package com.lhzw.searchlocmap.fragment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BDManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.LoRaManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egt.gis.bean.PointD;
import com.egt.gis.bean.RasterType;
import com.egt.gis.interfaces.Analyse;
import com.egt.gis.interfaces.Raster;
import com.gtmap.DefaultResourceProxyImpl;
import com.gtmap.common.GT_Field;
import com.gtmap.common.GT_GeoArithmetic;
import com.gtmap.common.IGT_Observer;
import com.gtmap.common.IGT_PropertySet;
import com.gtmap.egraphic.basesymbol.IGM_BaseLineFeature;
import com.gtmap.egraphic.edit.GM_EditGlobalFunc;
import com.gtmap.egraphic.engine.GM_MESSAGE_ENGINE;
import com.gtmap.egraphic.engine.GM_TypeDefines;
import com.gtmap.egraphic.engine.GraphicOverlay;
import com.gtmap.egraphic.engine.IGM_GraphicLayer;
import com.gtmap.egraphic.engine.PROP_NAME;
import com.gtmap.egraphic.panel.GM_PnlGlobalFunc;
import com.gtmap.events.LongPressEvent;
import com.gtmap.events.MapListener;
import com.gtmap.events.ScrollEvent;
import com.gtmap.events.TouchEvent;
import com.gtmap.events.ZoomEvent;
import com.gtmap.map.Config;
import com.gtmap.tileprovider.MapTileProviderBase;
import com.gtmap.tileprovider.MapTileProviderBasic;
import com.gtmap.tileprovider.tilesource.TileSourceFactory;
import com.gtmap.util.GeoPoint;
import com.gtmap.views.MapController;
import com.gtmap.views.MapView;
import com.gtmap.views.overlay.ItemizedIconOverlay;
import com.gtmap.views.overlay.ItemizedOverlayWithFocus;
import com.gtmap.views.overlay.OverlayItem;
import com.gtmap.views.overlay.ScaleBarOverlay;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.CommandAdapter;
import com.lhzw.searchlocmap.adapter.PlotHorizonAdapter;
import com.lhzw.searchlocmap.adapter.PortaitAdapter;
import com.lhzw.searchlocmap.adapter.SrollAdapter;
import com.lhzw.searchlocmap.adapter.TimerSearchAdapter;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bdsignal.BDSignal;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.LocTrackBean;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.bean.PlotItem;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.bean.RequestCommonBean;
import com.lhzw.searchlocmap.bean.SectionChartPoint;
import com.lhzw.searchlocmap.bean.TreeStateBean;
import com.lhzw.searchlocmap.bean.WatchLastLocTime;
import com.lhzw.searchlocmap.bean.WatchLocBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.event.EventBusBean;
import com.lhzw.searchlocmap.interfaces.OnHoriItemClickListener;
import com.lhzw.searchlocmap.overlay.DEMPointOverlay;
import com.lhzw.searchlocmap.overlay.MeasureAreaOverlay;
import com.lhzw.searchlocmap.overlay.MeasureDistanceOverlay;
import com.lhzw.searchlocmap.overlay.MeasureOverlayManager;
import com.lhzw.searchlocmap.overlay.OverlayFactory;
import com.lhzw.searchlocmap.overlay.P2pviewAnalysisOverlay;
import com.lhzw.searchlocmap.scrolllayout.ScrollLayout;
import com.lhzw.searchlocmap.ui.CommandStateActivity;
import com.lhzw.searchlocmap.ui.CommunicationListActivity;
import com.lhzw.searchlocmap.ui.LocationTrackActivity;
import com.lhzw.searchlocmap.ui.PerStateActivity;
import com.lhzw.searchlocmap.ui.PlotItemListActivity;
import com.lhzw.searchlocmap.ui.ShortMessUploadActivity;
import com.lhzw.searchlocmap.ui.TreeStateListActivity;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.ComUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.LogWrite;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.DragView;
import com.lhzw.searchlocmap.view.HistogramBar;
import com.lhzw.searchlocmap.view.HorizontalListView;
import com.lhzw.searchlocmap.view.LocationDialog;
import com.lhzw.searchlocmap.view.ScanAnimView;
import com.lhzw.searchlocmap.view.ShowAlertDialogCommand;
import com.lhzw.searchlocmap.view.ShowDialogSearch;
import com.lhzw.searchlocmap.view.ShowHandlePlotDialog;
import com.lhzw.searchlocmap.view.ShowSearchEndNoteDialog;
import com.lhzw.searchlocmap.view.ShowStateTreeDialog;
import com.lhzw.searchlocmap.view.ShowTimerCloseDialog;
import com.lhzw.searchlocmap.view.ShowTimerDialog;
import com.lhzw.searchlocmap.view.ShowUploadDetailDialog;
import com.lhzw.uploadmms.UploadInfoBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SecurityFragment extends BaseFragment implements IGT_Observer,
        LocationListener, OnHoriItemClickListener, ShowTimerDialog.onTimeItemClickListener,
        PopupWindow.OnDismissListener, PortaitAdapter.OnItemSelectedListener, AdapterView.OnItemLongClickListener,
        ShowStateTreeDialog.OnSearchCancelListener, MapListener, ScrollLayout.OnScrollChangedListener, SrollAdapter.OnClickScrollItemListener {
    private DrawerLayout drawer;
    private boolean list_h_state;
    private Animation am1;
    private Animation am2;
    private HorizontalListView rl_h_list;
    private List<PlotItem> landscapeList = new ArrayList<PlotItem>();
    private PlotHorizonAdapter plotHAdapter;
    private final int PERSTATE_REQCODE = 0x101;
    private LinearLayout ll_plot_tools;
    private LinearLayout ll_plot_function;
    private MeasureOverlayManager overlayManager = new MeasureOverlayManager();
    private MapView mMapView;
    private GraphicOverlay mGraphicOverlay;
    private LocationManager locManager;
    private boolean isCurrentApp;
    private boolean isFisre;
    private final int GPS_FAIL = 10001;
    private Criteria criteria;
    private double lon;
    private double lat;
    public static final String DEFAULT_LAYER_NAME = "默认标绘层";
    private static final String LOCATION_LAYER_NAME = "当前位置层";
    private static final String SOILDERINFO_LAYER_NAME = "SOS人员信息层";
    private static final String COMMONLIDERINFO_LAYER_NAME = "COMMON人员信息层";
    private ItemizedOverlayWithFocus locationOverlay;
    private int localFeature_id;
    private List<PersonalInfo> undetermined_List = new ArrayList<PersonalInfo>();
    private List<PersonalInfo> commonList = new ArrayList<PersonalInfo>();
    private List<PersonalInfo> sosList = new ArrayList<PersonalInfo>();
    private Dao<PersonalInfo, Integer> persondao;
    private DatabaseHelper<?> helper;
    private Map<String, String> map_sos;
    private Map<String, String> map_common;
    private List<Integer> sos_marker_id = new ArrayList<>();
    private List<Integer> common_marker_id = new ArrayList<>();
    private String[] searchTimeArr;
    private boolean isTimerRuning;
    private RelativeLayout ll_animation;
    private ImageView im_animation;
    private AnimationDrawable animation;
    private byte[] bytes;
    private LoRaManager loRaManager;
    private TextView note_content_tv;
    private PopupWindow mPopWindow;
    private ShowAlertDialogCommand dialogCommmand;
    private MapController mapController;
    private boolean isSettingEnable;
    private final int TIMER_REPORT = 0x0021;
    private byte[] codeArr;
    private Button bt_track_complete;
    private boolean isTrackState;
    private List<GeoPoint> trackList;
    private final int SYNC_DELAY = 2 * 60 * 1000;
    private final int SYNC_DELAY_SIGNAL = 0x0031;
    private Boolean isSyncState;
    private TextView tv_search_note;
    private Map<Integer, PersonalInfo> tatolMap;
    private int icon_id;
    private List<PlotItemInfo> firePlots;
    private Dao<PlotItemInfo, Integer> plotDao;
    private ListView plot_listView;
    private PortaitAdapter portaitAdapter;
    private int pathId = -1;
    private Dao<PlotItemInfo, Integer> plotItemDao;
    private Dao<LocTrackBean, Integer> locTrackDao;
    public static DecimalFormat df = new DecimalFormat("######0.0");
    private int syncFLId;
    private final int SEARCH = 0x0071;
    private long delay;
    private TextView tv_conmnication_num;
    private List<PlotItemInfo> newFirePlots;
    private Bitmap sos_bitmap;
    private Bitmap common_bitmap;
    private final int SEARCH_DELAY_CLOSE = 0x0091;
    private ShowDialogSearch searchDialog;
    private final int TOTAL_SEARCH_DELAY = 0x20081;
    private final int TOATAL_UPLOADING = 0x20082;
    private final int TOATAL_UPLOADED = 0x20083;
    private int sucess;
    private int fail;
    private int total;
    private ShowSearchEndNoteDialog searchEndnoteDialog;
    private boolean isUploading;
    private ShowTimerCloseDialog timerCloseDialog;
    private TextView tv_signal_search_note;
    private ComUtils mComUtils;
    private BDManager mBDManager;
    private byte[] bdByteArr;
    private boolean isShowing;
    private MapTileProviderBase tileProviderBase;
    private ShowHandlePlotDialog handleDialog;
    private int selectID;
    private LinearLayout ll_zoom_tools;
    private final int ZOOM_DIMISS = 0x0054;
    private String[] content = new String[]{"", "", "", "", "", "", "", "", "", ""};
    private final int SEARCH_NOTE = 0x6800;
    private ShowStateTreeDialog treeDialog;
    private final int TREE_REFLESH = 0x0043;
    private boolean isRuuning = false;
    private TextView tv_location;
    private Dao<TreeStateBean, Integer> treeDao;
    private boolean isUpload;
    private final int REFLESH_TV = 0x7700;
    private boolean demAnalysis;
    private PointD pd;
    private Dao<HttpPersonInfo, Integer> mHttpDao;
    private ShowUploadDetailDialog uploadDetail;
    private TreeStateBean uploadLocTimeBean;
    private Dao<WatchLastLocTime, Integer> locTimeDao;
    private ScrollLayout mScrollLayout;
    private boolean isOpen = false;
    private RelativeLayout rl_upload_inner;
    private Button im_upload_state_cancel;
    private RelativeLayout rl_upload_state_progress;
    private RelativeLayout rl_upload_outer;
    private ScanAnimView scanani_view;
    private HistogramBar his_bar;
    private TextView tv_signal_level;
    private ListView scroll_listview;
    private TextView tv_search_state;
    private TextView tv_serach_date;
    private TextView tv_serach_time;
    private ImageView rl_upload_time_select;
    private ImageView rl_upload_history;
    private SrollAdapter mScrollAdapter;
    private TextView tv_update_leisure;
    private View scroll_cancel;
    private TextView tv_upload_state;
//    private LoadingView loadingView;
    private final int COMPLETE = 0x0087;
    private Map<String, String> mHashMap;
    private Dao<MessageInfoIBean, Integer> mMsgDao;
    private RadioGroup mRadioGroup;
//    private ImageView mIvSwitchRg;
    private FrameLayout mFlContainer;
    private ImageView mIvOpen;


    private void initScrollView() {
        /**设置 setting*/
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset(1505);
        mScrollLayout.setExitOffset(0);
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setEnable(false);
        mScrollLayout.setAllowHorizontalScroll(false);
        mScrollLayout.setOnScrollChangedListener(this);
        mScrollLayout.setToExit();
        mScrollLayout.getBackground().setAlpha(0);
    }

    private void initDEMData() {
        String url = Environment.getExternalStorageDirectory().getPath();
        String configPath = url + "/gtmap/config4and/";
        //获取运行环境
        if (new File(configPath).exists()) {
            demAnalysis = com.egt.gis.interfaces.Map.setConfigPath(configPath);
        }
        if (demAnalysis) {
            com.egt.gis.interfaces.Map.initialize();
            //注册组件
            Raster.registerRasterComponent();
            Analyse.registerAnalyserComponent();
            //初始化DEM数据文件
            String url_tld = Config.MAP_ROOT_PATH + "/tiles/100wdem/100wDEM.tld";
            boolean load = Raster.loadRasterMapByFileName(url_tld, RasterType.RASTER_DEM);
            if (load) {
                showToast("高程数据初始化完成");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locManager != null) {
            startLocation();
        }
    }

    private void initMapConfig() {
        Config.DATA_CODE = "20190417";
        String mapPath = BaseUtils.getStoragePath();
        if (BaseUtils.isPathExist(mapPath)) {
            Config.MAP_ROOT_PATH = mapPath;
            Log.e("Tag", "Path : " + mapPath);
        }
        Config.MAX_LEVEL = 20;
    }

    private void initMap() {
        try {
            initMapConfig();
            FrameLayout map_layout = (FrameLayout) view.findViewById(R.id.map);
            if (tileProviderBase == null) {
                tileProviderBase = new MapTileProviderBasic(getActivity().getApplicationContext(), TileSourceFactory.getTileSourceByMapName("YX|ZJ"));
            }
            mMapView = new MapView(getActivity(), 256, new DefaultResourceProxyImpl(getActivity()), tileProviderBase);
            //设置是否手动控制缩放
            mMapView.setMultiTouchControls(true);
            //设置是否显示缩放按钮
            mMapView.setBuiltInZoomControls(false);
            //增加比例尺显示
            mMapView.getOverlayManager().add(new ScaleBarOverlay(getActivity()));
            mGraphicOverlay = new GraphicOverlay(getContext(), mMapView);
            GM_EditGlobalFunc.GM_GetEgraphicGlobalPropertySetPtr().SetBoolProperty("GM_EDIT2_KEEP_CREATE", true);
            //火点火线标绘层
            mGraphicOverlay.NewLayer(DEFAULT_LAYER_NAME);
            mGraphicOverlay.SetEditable(false, DEFAULT_LAYER_NAME);
            mGraphicOverlay.SetSelectable(false, DEFAULT_LAYER_NAME);
            mGraphicOverlay.SetLayerZoomWithMap(false, DEFAULT_LAYER_NAME);
            //人员位置显示层
            mGraphicOverlay.NewLayer(SOILDERINFO_LAYER_NAME);
            mGraphicOverlay.SetLayerZoomWithMap(false, SOILDERINFO_LAYER_NAME);
            mGraphicOverlay.SetSelectable(true, SOILDERINFO_LAYER_NAME);
            mGraphicOverlay.Attach(this, 0, SOILDERINFO_LAYER_NAME);
            //取消长按选择
            mGraphicOverlay.setLongPressEnable(false);
            //开启点击选择
            mGraphicOverlay.setTouchPickEnable(true);
            mMapView.getOverlayManager().add(mGraphicOverlay);
            pd = new PointD();
            mMapView.setMapListener(this);
//        mMapView.setBuiltInZoomControls(true);
            //定位层
            mGraphicOverlay.NewLayer(LOCATION_LAYER_NAME);
//        addLcoationLayer(new GeoPoint(lat, lon), radius);
            createMarker(new GeoPoint(lat, lon));
            //地图容器载入地图
            map_layout.addView(mMapView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            //控制初始显示中心点和缩放等级
            mapController = mMapView.getController();
            mapController.setZoom(16);
            mapController.setCenter(new GeoPoint(lat, lon));
            //在地图上绘制东西
            initSoilderInfoList();
            // 初始化火点火线
            initFirePFireL(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        } else {
            initMap();
        }
        boolean isLocation = false;
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            Log.e("Tag", "request location permission   ACCESS_COARSE_LOCATION");
        } else {
            isLocation = true;
        }
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            Log.e("Tag", "request location permission    ACCESS_FINE_LOCATION");
        } else {
            //两个权限都有了，申请权限成功
            if (isLocation) {
                startLocation();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Tag", "OnActlkasdjfklasjf  ....");
        super.onActivityResult(requestCode, resultCode, data);
        boolean isLocation = false;
        if (resultCode == 1) {
            int i = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (i == PackageManager.PERMISSION_GRANTED) {
                initMap();
            }
        } else if (resultCode == 2) {
            int i = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if (i == PackageManager.PERMISSION_GRANTED) {
                isLocation = true;
            }
            Log.e("Tag", "state : " + i + "  " + PackageManager.PERMISSION_GRANTED);
        } else if (resultCode == 3) {
            int i = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (isLocation && i == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            }
        } else if (resultCode == 4) {
            int markerId = data.getIntExtra("markerId", 0);
            icon_id = markerId;
            Log.e("Tag", "num " + data.getStringExtra("num") + "  " + tatolMap.get(markerId).getNum());
            if (tatolMap.get(markerId) != null) {
                if ((Constants.PERSON_COMMON).equals(tatolMap.get(markerId).getState())) {
                    showNormalPopupWindow(tatolMap.get(markerId));
                    setTouched(markerId);
                } else if ((Constants.PERSON_SOS).equals(tatolMap.get(markerId).getState())) {
                    codeArr = BaseUtils.getPerRegisterByteArr(tatolMap.get(markerId).getNum());
                    byte[] numByte = obtainBDNum();
                    for (int j = 0; j < 4; j++) {
                        codeArr[5 + j] = numByte[j];
                    }
                    showSosPopupWindow(tatolMap.get(markerId));
                    setTouched(markerId);
                }
                mapController = mMapView.getController();
                mapController.setCenter(new GeoPoint(Double.valueOf(tatolMap.get(markerId).getLatitude()), Double.valueOf(tatolMap.get(markerId).getLongitude())));
            }
        } else if (resultCode == 5) {
            String num = data.getStringExtra("num");
            codeArr = BaseUtils.getPerRegisterByteArr(num);
            byte[] numByte = obtainBDNum();
            for (int j = 0; j < 4; j++) {
                codeArr[5 + j] = numByte[j];
            }
//            SpUtils.putBoolean(SPConstants.COMMON_SWITCH, true);
            sendCMDSearch(codeArr);
            showToast(getString(R.string.send_command_success_note));
            mHandler.removeMessages(SEARCH_DELAY_CLOSE);
            mHandler.sendEmptyMessageDelayed(SEARCH_DELAY_CLOSE, 10000);
        } else if (resultCode == -1) {
            Log.e("Tag", "result code");
            if (firePlots.size() > 0) {
                deleteFirePFireL();
                firePlots.clear();
            }
            initFirePFireL(-1);
        } else if (resultCode == 6) {
            initMessageNum();
        } else if (resultCode == 7) {
            //结束搜索
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (1 == requestCode) {
            if (permissions[0].equals(android.Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && permissions[1].equals(android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initMap();
            }
        }
    }

    @Override
    protected void onReflesh() {
        initSoilderInfoList();
        initMessageNum();
    }

    @Override
    protected synchronized void initSoilderInfoList() {
        //获取数据库信息
        perStateList();
        showSoilderInMap();
        mScrollAdapter.refleshView();
    }

    private synchronized void deleteFirePFireL() {

        if (firePlots.size() > 0) {
            for (PlotItemInfo item : firePlots) {
                if (item.getMakerId() != -1) {
                    mGraphicOverlay.DeleteFeature(item.getMakerId(), DEFAULT_LAYER_NAME);
                }
            }
        }
        refreshMap();
    }

    private synchronized void deleteFirePFireL(List<PlotItemInfo> list) {
        if (list.size() > 0) {
            for (PlotItemInfo item : list) {
                if (item.getMakerId() != -1) {
                    mGraphicOverlay.DeleteFeature(item.getMakerId(), DEFAULT_LAYER_NAME);
                }
            }
        }
        list.clear();
        refreshMap();
    }

    protected synchronized void initFirePFireL(int position) {
        List<PlotItemInfo> plotList = CommonDBOperator.getList(plotDao);
        if (plotList != null && plotList.size() > 0) {
            firePlots.addAll(plotList);
            plotList.clear();
        }
        for (int i = 0; i < firePlots.size(); i++) {
            if (firePlots.get(i).getData_type() == Constants.TX_FIREPOIT) {
                GeoPoint[] fpList = BaseUtils.stringToGeoPoint(firePlots.get(i).getPaths());
                if (fpList != null) {
                    firePlots.get(i).setMakerId(drawFirePoint(fpList[0]));
                    CommonDBOperator.updateItem(plotDao, firePlots.get(i));
                }
            } else if (firePlots.get(i).getData_type() == Constants.TX_FIRELINE) {
                GeoPoint[] flList = BaseUtils.stringToGeoPoint(firePlots.get(i).getPaths());
                Log.e("Tag", "draw list");
                if (flList != null) {
                    firePlots.get(i).setMakerId(drawFireLine(flList, Constants.TX_FIRELINE));
                    CommonDBOperator.updateItem(plotDao, firePlots.get(i));
                }
            } else if (firePlots.get(i).getData_type() == Constants.TX_SYNCFL) {
                GeoPoint[] flList = BaseUtils.stringToGeoPoint(firePlots.get(i).getPaths());
                Log.e("Tag", "draw sync");
                if (flList != null) {
                    firePlots.get(i).setMakerId(drawFireLine(flList, Constants.TX_SYNCFL));
                    CommonDBOperator.updateItem(plotDao, firePlots.get(i));
                }
            }
        }
        refreshMap();
        portaitAdapter.initPlotItem();
        portaitAdapter.setSelectID(position);
        portaitAdapter.notifyDataSetChanged();
    }

    @Override
    protected void drawSyncFireLine() {
        // 绘制火线
        isSyncState = false;
        tv_search_note.setText("");
        showToast(getString(R.string.sync_fire_line_success));

        if (firePlots.size() > 0) {
            deleteFirePFireL();
            firePlots.clear();
        }
        initFirePFireL(-1);
        //重新绘制

//        if (syncFLId != -1) {
//            mGraphicOverlay.DeleteFeature(syncFLId, DEFAULT_LAYER_NAME);
//        }
//        drawSyncFireLine(BaseUtils.stringToGeoPoint(paths));
    }

    // 获取在线人员和离线人员的list

    public void perStateList() {
        sucess = 0;
        fail = 0;
        total = 0;
        if (undetermined_List != null) {
            undetermined_List.clear();
        }
        if (commonList != null) {
            commonList.clear();
        }
        if (sosList != null) {
            sosList.clear();
        }
        List<PersonalInfo> tmpCommonList = CommonDBOperator.queryByKeys(persondao, "state", Constants.PERSON_COMMON);
        if (tmpCommonList != null && tmpCommonList.size() > 0) {
            commonList.addAll(tmpCommonList);
            sucess += commonList.size();
            tmpCommonList.clear();
        }
        List<PersonalInfo> tmpSosList = CommonDBOperator.queryByKeysOrderByTime(persondao, "state", Constants.PERSON_SOS, "time");
        if (tmpSosList != null && tmpSosList.size() > 0) {
            sosList.addAll(tmpSosList);
            sucess += sosList.size();
            tmpSosList.clear();
        }
        // 初始化待定区域
        List<PersonalInfo> undermined_sos_list = CommonDBOperator.queryByMultiKeys(persondao, map_sos);
        if (undermined_sos_list != null && undermined_sos_list.size() > 0) {
            undetermined_List.addAll(undermined_sos_list);
            sucess += undermined_sos_list.size();
            undermined_sos_list.clear();
        }

        List<PersonalInfo> undermined_common_list = CommonDBOperator.queryByMultiKeys(persondao, map_common);
        if (undermined_common_list != null && undermined_common_list.size() > 0) {
            undetermined_List.addAll(undermined_common_list);
            sucess += undermined_common_list.size();
            undermined_common_list.clear();
        }
        List<PersonalInfo> offline_list = CommonDBOperator.queryByKeys(persondao, "state", Constants.PERSON_OFFLINE);// 离线
        if (offline_list != null && offline_list.size() > 0) {
            undetermined_List.addAll(offline_list);
            fail = offline_list.size();
            offline_list.clear();
        }
        total = sucess + fail;
//        undetermined_adpter.refleshList();
    }

    //绘制sos common
    private void showSoilderInMap() {
        //清除地图上的所有sos common
        if (sos_marker_id.size() > 0) {
            for (Integer id : sos_marker_id) {
                mGraphicOverlay.DeleteFeature(id, SOILDERINFO_LAYER_NAME);
            }
        }

        if (common_marker_id.size() > 0) {
            for (Integer id : common_marker_id) {
                mGraphicOverlay.DeleteFeature(id, SOILDERINFO_LAYER_NAME);
            }
        }
        if (tatolMap.size() > 0) {
            tatolMap.clear();
        }

        if (sos_marker_id.size() > 0) {
            sos_marker_id.clear();
        }

        if (common_marker_id.size() > 0) {
            common_marker_id.clear();
        }

        //绘制sos
        if (sosList != null && sosList.size() > 0) {
            for (PersonalInfo item : sosList) {
                int plot_id = mGraphicOverlay.AddPicturePlaceMark(new GeoPoint(Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())), sos_bitmap, item.getName(), 20.0, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureProp(plot_id, PROP_NAME.SpointAnnoFontSize, "8", SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureProp(plot_id, PROP_NAME.SPointAnnoFontColor, "" + Color.RED, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureProp(plot_id, PROP_NAME.SPointAnnoTextPos, "0", SOILDERINFO_LAYER_NAME);
//            mGraphicOverlay.setModified();
//            mMapView.postInvalidate();
                mGraphicOverlay.SetFeatureExtendPropString(plot_id, "String_PROP", Constants.PERSON_SOS, SOILDERINFO_LAYER_NAME);
                sos_marker_id.add(plot_id);
                tatolMap.put(plot_id, item);

                item.setMarkerId(plot_id);
                CommonDBOperator.updateItem(persondao, item);
            }
        }

        //绘制common
        if (commonList != null && commonList.size() > 0) {
            for (PersonalInfo item : commonList) {
                int plot_id = mGraphicOverlay.AddPicturePlaceMark(new GeoPoint(Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())), common_bitmap, item.getName(), 20.0, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureExtendPropString(plot_id, "String_PROP", Constants.PERSON_COMMON, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureProp(plot_id, PROP_NAME.SpointAnnoFontSize, "8", SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureProp(plot_id, PROP_NAME.SPointAnnoFontColor, "" + Color.RED, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SetFeatureProp(plot_id, PROP_NAME.SPointAnnoTextPos, "0", SOILDERINFO_LAYER_NAME);
//            mGraphicOverlay.setModified();
//            mMapView.postInvalidate();
                common_marker_id.add(plot_id);
                tatolMap.put(plot_id, item);

                item.setMarkerId(plot_id);
                CommonDBOperator.updateItem(persondao, item);
            }
        }
        mGraphicOverlay.setModified();
        mMapView.postInvalidate();
    }

    @Override
    public void onClick(View v) {
        setTouched(-1);
//        super.onClick(v);
        switch (v.getId()) {
            case R.id.communication_btn:
                startActivity(new Intent(getActivity(), CommunicationListActivity.class));
                break;
            case R.id.bt_plot_list:
                drawer.setScrimColor(Color.TRANSPARENT);
                drawer.openDrawer(Gravity.RIGHT);
                Log.e("Tag", "bt_plot_list");
                break;
            case R.id.total_search_btn:
//                if (isTimerRuning) {
//                    timerCloseDialog = new ShowTimerCloseDialog(getActivity());
//                    timerCloseDialog.show();
//                    timerCloseDialog.setListener(this);
//                    return;
//                }
                if (isOpen) {
                    mScrollLayout.scrollToExit();
                } else {
                    mScrollLayout.setToOpen();
                    scroll_cancel.setVisibility(View.VISIBLE);
                }
//                showSearchDialog();
                break;
            case R.id.switch_location_btn:
                try {
                    //将镜头平移到当前手持机定位的中心
                    mapController = mMapView.getController();
                    mapController.setCenter(new GeoPoint(lat, lon));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            case R.id.bd_service_btn:
                try {
                    startActivity(new Intent("com.lhzw.intent.action_UPLOAD_SERVICE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.im_person_state:
                startActivityForResult(new Intent(getActivity(), PerStateActivity.class), PERSTATE_REQCODE);
                break;
            case R.id.tv_function:
                if (isSettingEnable) {
                    showToast(getString(R.string.function_ennable_note));
                    return;
                }
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.setScrimColor(getResources().getColor(R.color.tra_gray));
                    drawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.rl_mesure_distance:
                isSettingEnable = true;
                startLeftAnimation(false);
                drawer.closeDrawer(Gravity.LEFT);
                measureDistance();
                break;
            case R.id.rl_mesure_area:
                isSettingEnable = true;
                startLeftAnimation(false);
                drawer.closeDrawer(Gravity.LEFT);
                measureArea();
                break;
            case R.id.rl_mesure_height:
                isSettingEnable = true;
                startLeftAnimation(false);
                drawer.closeDrawer(Gravity.LEFT);
                measureDEMPoint();
                break;
            case R.id.rl_mesure_p2p:
                isSettingEnable = true;
                startLeftAnimation(false);
                drawer.closeDrawer(Gravity.LEFT);
                p2pViewAnalysis();
                break;
            case R.id.rl_plot:
                isSettingEnable = true;
                drawer.closeDrawer(Gravity.LEFT);
                plotHAdapter.resetState();
                startPlotLeftAnimation(false);
                startBottomAnimation(list_h_state);
                break;
            case R.id.rl_plot_list:
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
                startActivityForResult(new Intent(getActivity(), PlotItemListActivity.class), 0x0061);
                break;
            case R.id.rl_loc_track:
                Log.e("Tag", "tv_loc_track");
                isSettingEnable = true;
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
                isTrackState = true;
                trackList.clear();
                startLeftTrackAnimation(false);
                showToast(getString(R.string.track_start));
//                tv_search_note.setText(getString(R.string.record_track_begaining));
//                tv_search_note.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_track_list:
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
                startActivity(new Intent(getActivity(), LocationTrackActivity.class));
                Log.e("Tag", "tv_track_list");
                break;
            case R.id.rl_quick_loc:
                Log.e("Tag", "tv_quick_loc");
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
                LocationDialog locationDialog = new LocationDialog(getActivity(), mMapView);
                Window window1 = locationDialog.getWindow();
                window1.setGravity(Gravity.BOTTOM);
                window1.setWindowAnimations(R.style.dialog_animation);
                locationDialog.show();
                WindowManager windowManager = getActivity().getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = locationDialog.getWindow().getAttributes();
                lp.width = (int) (display.getWidth()); //设置宽度
                locationDialog.getWindow().setAttributes(lp);
                locationDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                locationDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;
            case R.id.rl_sync_fire_line:
                if (BaseUtils.getDBManager(getActivity()) == null) {
                    return;
                }
                //同步火线
                if (isSyncState) {
                    showToast(getString(R.string.sync_fire_line_starting));
                    return;
                }
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
                tv_search_note.setText(getString(R.string.request_fire_line_begaining));
                tv_search_note.setVisibility(View.VISIBLE);
                isSyncState = true;
                mHandler.sendEmptyMessageDelayed(SYNC_DELAY_SIGNAL, SYNC_DELAY);
                try {
                    SearchLocMapApplication.getInstance().getUploadService().reuestData(lat, lon);
                    // 写入日志
                    LogWrite writer = LogWrite.open();
                    String log = LogWrite.df.format(System.currentTimeMillis()) + " \t data_type = " + Constants.TX_COMMAND + "\t lat = " + lat + "\t lon = " + lon;
                    writer.writeLog(log);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_plot_back:
                Log.e("Tag", "bt_plot_back");
                int type = overlayManager.getType();
                if (type != -1) {
                    overlayManager.getOverlay(type).repealLastPoint();
                    mMapView.postInvalidate();
                }
                break;
            case R.id.bt_plot_complete:
                isSettingEnable = false;
                startLeftAnimation(true);
                overlayManager.removeOverlays(mMapView.getOverlayManager());
                mMapView.postInvalidate();
                break;
            case R.id.bt_plot_move:
                Log.e("Tag", "bt_plot_move");
                plotHAdapter.resetState();
                mGraphicOverlay.SelectTool(-1, mMapView);
                break;
            case R.id.bt_plot_save:
                Log.e("Tag", "bt_plot_save");
                insertPlotsToDataBase(newFirePlots);
                firePlots.addAll(newFirePlots);
                newFirePlots.clear();
                //刷新plotlist
                portaitAdapter.initPlotItem();
                portaitAdapter.notifyDataSetChanged();
                showToast("保存完成");
                isSettingEnable = false;
                startPlotLeftAnimation(true);
                mGraphicOverlay.SetSelectable(true, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SelectTool(-1, mMapView);
                startBottomAnimation(true);
                rl_h_list.setVisibility(View.GONE);
                break;
            case R.id.bt_plot_over:
                Log.e("Tag", "bt_plot_over");
                deleteFirePFireL(newFirePlots);
                isSettingEnable = false;
                startPlotLeftAnimation(true);
                mGraphicOverlay.SetSelectable(true, SOILDERINFO_LAYER_NAME);
                mGraphicOverlay.SelectTool(-1, mMapView);
                startBottomAnimation(true);
                rl_h_list.setVisibility(View.GONE);
                break;
            case R.id.bt_track_complete:
                isSettingEnable = false;
                startLeftTrackAnimation(true);
                showToast(getString(R.string.track_stop));
                isTrackState = false;
                Log.e("Tag", "bt_track_complete");
                //保存 清空数据
                if (trackList.size() > 0) {
                    LocTrackBean item = new LocTrackBean("", System.currentTimeMillis(), BaseUtils.geoPointToString(trackList));
                    CommonDBOperator.saveToDB(locTrackDao, item);
                }
                deleteLocTrack();
                break;
            case R.id.tv_command_cancel:
                dialogCommmand.clear();
                break;
            case R.id.tv_state://查看指令状态
                dialogCommmand.clear();//dialog消失
                Intent intent = new Intent(getActivity(), CommandStateActivity.class);
                startActivity(intent);
                break;

            // 下达指令

            case R.id.normal_mass_bt:
                sendCMDAction(codeArr, (byte) 0x03);
                mPopWindow.dismiss();
                mPopWindow = null;
                showToast(getString(R.string.send_command_success_note));
                break;
            case R.id.normal_act_bt:
                sendCMDAction(codeArr, (byte) 0x02);
                mPopWindow.dismiss();
                mPopWindow = null;
                showToast(getString(R.string.send_command_success_note));
                break;
            case R.id.normal_cross_bt:
                sendCMDAction(codeArr, (byte) 0x01);
                mPopWindow.dismiss();
                mPopWindow = null;
                showToast(getString(R.string.send_command_success_note));
                break;
            case R.id.pop_rescue_bt:
                isSosFlash = false;
                sendCMDSOSComplete(codeArr);

                if(BaseUtils.isNetConnected(SearchLocMapApplication.getContext())) {
                    double latSos = 0.0;
                    double lngSos = 0.0;
                    if(!"".equals(tatolMap.get(icon_id).getLatitude()) && !"".equals(tatolMap.get(icon_id).getLongitude())
                            && !"null".equals(tatolMap.get(icon_id).getLatitude()) && !"null".equals(tatolMap.get(icon_id).getLongitude())){
                        latSos = Double.valueOf(tatolMap.get(icon_id).getLatitude());
                        lngSos = Double.valueOf(tatolMap.get(icon_id).getLongitude());
                    }
                    WatchLocBean bean = new WatchLocBean("watch", tatolMap.get(icon_id).getOffset() + "", "", "", "sosfinished", "", latSos + "", lngSos + "", BaseUtils.sdf.format(tatolMap.get(icon_id).getLocTime()));
                    List<WatchLocBean> sosListUpload = new ArrayList<>();
                    sosListUpload.add(bean);
                    RequestCommonBean sosBean = new RequestCommonBean(Constants.CMD_SOS, "handsetsession", "HANDSET",
                            BaseUtils.getDipperNum(SearchLocMapApplication.getContext()), SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT),
                            SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON), BaseUtils.sdf.format(SpUtils.getLong(SPConstants.LOC_TIME,
                            System.currentTimeMillis())), sosListUpload);
                    ComUtils.getInstance().uploadToNet(sosBean, null);
                }

                updataState(icon_id);
                mPopWindow.dismiss();
                mPopWindow = null;
                showToast(getString(R.string.send_command_success_note));
                break;
            case R.id.dialog_timer_search:

                break;
            case R.id.dialog_total_search:
                if (total == 0) {
                    showToast(getString(R.string.note_input_person_note));
                    return;
                }
                timerSearch();
                searchDialog.dismiss();
                break;
            case R.id.dialog_history:
                startActivity(new Intent(getActivity(), TreeStateListActivity.class));
                searchDialog.dismiss();
                break;
            case R.id.dialog_upload:
                if (BaseUtils.getDBManager(getActivity()) == null) {
                    return;
                }
                try {
                    Log.e("Tag", "dialog_upload upload ....");
                    showSearchToast(getString(R.string.upload_TE_note));
                    searchEndnoteDialog.dismiss();
                    mHandler.sendEmptyMessageDelayed(TIMER_REPORT, 8000);
                    mHandler.sendEmptyMessageDelayed(TOATAL_UPLOADING, 4000);
                    mHandler.sendEmptyMessageDelayed(TOATAL_UPLOADED, 9000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dialog_timer_close:
                try {
                    timerCloseDialog.dismiss();
                    showSearchToast(getString(R.string.timer_search_end_note));
                    isTimerRuning = false;
                    rl_upload_inner.setEnabled(true);
                    stopAnimation();
                    note_content_tv.setText("");
                    mHandler.removeMessages(SEARCH);
//                    SpUtils.putBoolean(SPConstants.COMMON_SWITCH, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dialog_timer_cancel:
                tv_signal_search_note.setVisibility(View.GONE);
                tv_signal_search_note.setText("");
                searchEndnoteDialog.dismiss();
                isTimerRuning = false;
                isUploading = false;
                break;
            case R.id.dialog_plot_upload:
                uploadItem();
                handleDialog.dismiss();
                break;
            case R.id.dialog_plot_del:
                deletePlotItem();
                handleDialog.dismiss();
                break;
            case R.id.bt_zoom_amplifier:
                Log.e("Tag", "bt_zoom_amplifier");
                mHandler.removeMessages(ZOOM_DIMISS);
                mHandler.sendEmptyMessageDelayed(ZOOM_DIMISS, 3000);
                mMapView.getController().zoomIn();
                break;
            case R.id.bt_zoom_shrink:
                Log.e("Tag", "bt_zoom_shrink");
                mHandler.removeMessages(ZOOM_DIMISS);
                mHandler.sendEmptyMessageDelayed(ZOOM_DIMISS, 3000);
                mMapView.getController().zoomOut();
                break;
            case R.id.dialog_search_upload:
                isUploading = true;
                treeDialog = new ShowStateTreeDialog(getActivity());
                treeDialog.showDialog();
                WindowManager.LayoutParams params = treeDialog.getWindow().getAttributes();
                params.width = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 80;
                params.height = 644;
                treeDialog.getWindow().setAttributes(params);
                treeDialog.setOnSearchCancelListener(this);
                treeDialog.setEnable(false);
                isUpload = true;
                tatolSearch();
                uploadDetail.dismiss();
                uploadDetail = null;
                break;
            case R.id.rl_upload_inner:
                im_upload_state_cancel.setText(getString(R.string.upload_state_cancel_monitor));
                BaseUtils.flipAnimatorXViewShow(rl_upload_outer, rl_upload_state_progress, 200);
                scanani_view.startAnimation();
                tv_update_leisure.setVisibility(View.GONE);
                isUpload = true;
                tatolSearch();
                break;
            case R.id.im_upload_state_cancel:
                if(isUpload) {
                    isUpload = false;
                    mHandler.removeMessages(SEARCH_NOTE);
                    mHandler.removeMessages(TIMER_REPORT);
                    mHandler.removeMessages(COMPLETE);
                }
                if(isTimerRuning) {
                    isTimerRuning = false;
                    mHandler.removeMessages(REFLESH_TV);
                }
                BaseUtils.flipAnimatorXViewShow(rl_upload_state_progress, rl_upload_outer, 200);
                scanani_view.stopAnimation();
                tv_search_state.setText("");
                tv_serach_date.setText("");
                tv_serach_time.setText("");
                tv_update_leisure.setVisibility(View.VISIBLE);
                break;
            case R.id.scroll_cancel:
                if (isOpen) {
                    mScrollLayout.setToExit();
                    mScrollLayout.getBackground().setAlpha(0);
                }
                break;
            case R.id.rl_upload_time_select:
                timerSearch();
                mScrollLayout.setToExit();
                mScrollLayout.getBackground().setAlpha(0);
                break;
            case R.id.rl_upload_history:
                // 单次搜索
                if(sosList.size() == 0 && commonList.size() == 0 && undetermined_List.size() == 0) {
                    showToast(getString(R.string.total_search_person_none));
                    return;
                }
                cleanDatabase();
                im_upload_state_cancel.setText(getString(R.string.upload_cancel_search));
                BaseUtils.flipAnimatorXViewShow(rl_upload_outer, rl_upload_state_progress, 200);
                scanani_view.startAnimation();
                tv_update_leisure.setVisibility(View.GONE);
                isTimerRuning = true;
                tv_upload_state.setText(getString(R.string.upload_search_ing));
                new Thread(new Action()).start();
                break;
        }
    }

    @Override
    protected int initView() {
        return R.layout.security_fg;
    }
    private boolean isShow;//是否显示radioGroup
    private DragView mDragView;
    @SuppressLint("WrongConstant")
    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        helper = DatabaseHelper.getHelper(getActivity());
        mHashMap = new HashMap<>();
        mHashMap.put("type", ShortMessUploadActivity.MESSAGE_RECEIVE + "");
        mHashMap.put("state", Constants.MESSAGE_UNREAD + "");
        mMsgDao = helper.getMesgInfoDao();

        drawer = (DrawerLayout) view.findViewById(R.id.drawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mFlContainer.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mFlContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //initPopWindow();
//        mDragView = (DragView)view.findViewById(R.id.drag_view );
//        final RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.rl_container);
//        mDragView.setOnDragViewClickListener(new DragView.onDragViewClickListener() {
//            @Override
//            public void onDragViewClick() {
//                mDragView.playSoundEffect(SoundEffectConstants.CLICK);
//                mPopMemuWindow.showAtLocation(relativeLayout, Gravity.CENTER,0,0);
//                mDragView.setVisibility(View.GONE);
//                //Toast.makeText(getActivity(),"点击按钮",Toast.LENGTH_SHORT).show();
//            }
//        });
//        mIvSwitchRg = (ImageView) view.findViewById(R.id.iv_switch_rg);
//        mIvSwitchRg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isShow){
//                   mRadioGroup.setVisibility(View.GONE);
//                   mIvSwitchRg.setImageDrawable(getResources().getDrawable(R.drawable.icon_open));
//                }else {
//                    mRadioGroup.setVisibility(View.VISIBLE);
//                    mIvSwitchRg.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
//                }
//                isShow=!isShow;
//
//            }
//        });
        //网络通信模式选择
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rg_btn_bd:
                       SpUtils.putInt(SPConstants.COM_MODE,Constants.COM_MODE_BD);
                        break;
                    case R.id.rg_btn_net:
                        SpUtils.putInt(SPConstants.COM_MODE,Constants.COM_MODE_NET);
                        break;
                    case R.id.rg_btn_auto:
                        SpUtils.putInt(SPConstants.COM_MODE,Constants.COM_MODE_AUTO);
                        break;
                }
                if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                    try {
                        SearchLocMapApplication.getInstance().getUploadService().setCom(SpUtils.getInt(SPConstants.COM_MODE,Constants.COM_MODE_BD));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        switch (SpUtils.getInt(SPConstants.COM_MODE,Constants.COM_MODE_BD)){
            case Constants.COM_MODE_BD :
                mRadioGroup.check(R.id.rg_btn_bd);
                break;
            case Constants.COM_MODE_NET:
                mRadioGroup.check(R.id.rg_btn_net);
                break;
            case Constants.COM_MODE_AUTO:
                mRadioGroup.check(R.id.rg_btn_auto);
                break;
        }

        if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
            try {
                SearchLocMapApplication.getInstance().getUploadService().setCom(SpUtils.getInt(SPConstants.COM_MODE, Constants.COM_MODE_BD));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        LogUtil.d("当前网络模式为"+SpUtils.getInt(SPConstants.COM_MODE,Constants.COM_MODE_BD));

        mScrollLayout = (ScrollLayout) view.findViewById(R.id.scrolllayout);

        Button communication_btn = (Button) view.findViewById(R.id.communication_btn);
        communication_btn.setOnClickListener(this);

        Button bt_plot_list = (Button) view.findViewById(R.id.bt_plot_list);
        bt_plot_list.setOnClickListener(this);

        Button total_search_btn = (Button) view.findViewById(R.id.total_search_btn);
        total_search_btn.setOnClickListener(this);

        Button switch_location_btn = (Button) view.findViewById(R.id.switch_location_btn);
        switch_location_btn.setOnClickListener(this);
        //bd_service_btn
        Button btnBdService = (Button) view.findViewById(R.id.bd_service_btn);
        btnBdService.setOnClickListener(this);

        ImageView im_person_state = (ImageView) view.findViewById(R.id.im_person_state);
        im_person_state.setOnClickListener(this);

        TextView tv_function = (TextView) view.findViewById(R.id.tv_function);
        tv_function.setOnClickListener(this);

        LinearLayout rl_mesure_distance = (LinearLayout) view.findViewById(R.id.rl_mesure_distance);
        rl_mesure_distance.setOnClickListener(this);

        LinearLayout rl_mesure_area = (LinearLayout) view.findViewById(R.id.rl_mesure_area);
        rl_mesure_area.setOnClickListener(this);

        LinearLayout rl_mesure_height = (LinearLayout) view.findViewById(R.id.rl_mesure_height);
        rl_mesure_height.setOnClickListener(this);

        LinearLayout rl_mesure_p2p = (LinearLayout) view.findViewById(R.id.rl_mesure_p2p);
        rl_mesure_p2p.setOnClickListener(this);

        LinearLayout rl_plot = (LinearLayout) view.findViewById(R.id.rl_plot);
        rl_plot.setOnClickListener(this);

        LinearLayout rl_plot_list = (LinearLayout) view.findViewById(R.id.rl_plot_list);
        rl_plot_list.setOnClickListener(this);

        LinearLayout rl_loc_track = (LinearLayout) view.findViewById(R.id.rl_loc_track);
        rl_loc_track.setOnClickListener(this);

        LinearLayout rl_track_list = (LinearLayout) view.findViewById(R.id.rl_track_list);
        rl_track_list.setOnClickListener(this);

        LinearLayout rl_quick_loc = (LinearLayout) view.findViewById(R.id.rl_quick_loc);
        rl_quick_loc.setOnClickListener(this);

        LinearLayout rl_sync_fire_line = (LinearLayout) view.findViewById(R.id.rl_sync_fire_line);
        rl_sync_fire_line.setOnClickListener(this);

        Button bt_zoom_amplifier = (Button) view.findViewById(R.id.bt_zoom_amplifier);
        bt_zoom_amplifier.setOnClickListener(this);

        Button bt_zoom_shrink = (Button) view.findViewById(R.id.bt_zoom_shrink);
        bt_zoom_shrink.setOnClickListener(this);

        ll_zoom_tools = (LinearLayout) view.findViewById(R.id.ll_zoom_tools);

        tv_location = (TextView) view.findViewById(R.id.tv_location);

        tv_conmnication_num = (TextView) view.findViewById(R.id.tv_conmnication_num);
        tv_search_note = (TextView) view.findViewById(R.id.tv_search_note);
        tv_signal_search_note = (TextView) view.findViewById(R.id.tv_signal_search_note);

        bt_track_complete = (Button) view.findViewById(R.id.bt_track_complete);
        bt_track_complete.setOnClickListener(this);
        ll_plot_tools = (LinearLayout) view.findViewById(R.id.ll_plot_tools);
        rl_h_list = (HorizontalListView) view.findViewById(R.id.rl_h_list);
        ll_animation = (RelativeLayout) view.findViewById(R.id.ll_animation);
        im_animation = (ImageView) view.findViewById(R.id.im_animation);
        im_animation.setImageResource(R.drawable.persons_animation);
        Button bt_plot_back = (Button) view.findViewById(R.id.bt_plot_back);
        note_content_tv = (TextView) view.findViewById(R.id.note_content_tv);
        bt_plot_back.setOnClickListener(this);

        Button bt_plot_complete = (Button) view.findViewById(R.id.bt_plot_complete);
        bt_plot_complete.setOnClickListener(this);

        plot_listView = (ListView) view.findViewById(R.id.plot_listView);
        ll_plot_function = (LinearLayout) view.findViewById(R.id.ll_plot_function);
        Button bt_plot_move = (Button) view.findViewById(R.id.bt_plot_move);
        bt_plot_move.setOnClickListener(this);
        Button bt_plot_save = (Button) view.findViewById(R.id.bt_plot_save);
        bt_plot_save.setOnClickListener(this);
        Button bt_plot_over = (Button) view.findViewById(R.id.bt_plot_over);
        bt_plot_over.setOnClickListener(this);
//        HorizontalListView undetermined_listview = (HorizontalListView) view.findViewById(R.id.sos_horizon_listview);
//        undetermined_adpter = new UndeterminedAdapter(getActivity(), undetermined_List, SecurityFragment.this);
//        undetermined_listview.setAdapter(undetermined_adpter);

        rl_upload_inner = (RelativeLayout) view.findViewById(R.id.rl_upload_inner);
        im_upload_state_cancel = (Button) view.findViewById(R.id.im_upload_state_cancel);
        rl_upload_state_progress = (RelativeLayout) view.findViewById(R.id.rl_upload_state_progress);
        rl_upload_outer = (RelativeLayout) view.findViewById(R.id.rl_upload_outer);
        rl_upload_inner.setOnClickListener(this);
        im_upload_state_cancel.setOnClickListener(this);
        scanani_view = (ScanAnimView) view.findViewById(R.id.scanani_view);

        his_bar = (HistogramBar) view.findViewById(R.id.his_bar);
        tv_signal_level = (TextView) view.findViewById(R.id.tv_signal_level);
        scroll_listview = (ListView) view.findViewById(R.id.scroll_listview);
        tv_search_state = (TextView) view.findViewById(R.id.tv_search_state);
        tv_serach_date = (TextView) view.findViewById(R.id.tv_serach_date);
        tv_serach_time = (TextView) view.findViewById(R.id.tv_serach_time);
        rl_upload_time_select = (ImageView) view.findViewById(R.id.rl_upload_time_select);
        rl_upload_time_select.setOnClickListener(this);
        rl_upload_history = (ImageView) view.findViewById(R.id.rl_upload_history);
        rl_upload_history.setOnClickListener(this);
        TextView tv_per_num = (TextView) view.findViewById(R.id.tv_per_num);
        TextView tv_update_num = (TextView) view.findViewById(R.id.tv_update_num);
        TextView tv_sos_num = (TextView) view.findViewById(R.id.tv_sos_num);
        tv_update_leisure = (TextView) view.findViewById(R.id.tv_update_leisure);
        tv_upload_state = (TextView) view.findViewById(R.id.tv_upload_state);
        scroll_cancel = view.findViewById(R.id.scroll_cancel);
        scroll_cancel.setOnClickListener(this);
        mScrollAdapter = new SrollAdapter(getActivity(), sosList, commonList, undetermined_List, tv_per_num, tv_update_num, tv_sos_num);
        scroll_listview.setAdapter(mScrollAdapter);
        scroll_listview.setOnItemClickListener(mScrollAdapter);
        scroll_listview.setOnItemLongClickListener(mScrollAdapter);
        mScrollAdapter.setOnClickScrollItemListener(this);

        initMenuBtnListener();


        mHttpDao = helper.getHttpPerDao();
        persondao = helper.getPersonalInfoDao();
        plotDao = helper.getPlotItemDao();
        treeDao = helper.getTreeStateDao();
        locTimeDao = helper.getLastLocTimeDao();
        plotItemDao = helper.getPlotItemDao();
        locTrackDao = helper.getLocTrackDao();
        mComUtils = ComUtils.getInstance();
        map_sos = new HashMap<String, String>();
        firePlots = new ArrayList<>();
        newFirePlots = new ArrayList<>();
        map_sos.put("state", Constants.PERSON_UNDETERMINED);
        map_sos.put("state1", Constants.PERSON_SOS);
        trackList = new ArrayList<>();
        map_common = new HashMap<String, String>();
        map_common.put("state", Constants.PERSON_UNDETERMINED);
        map_common.put("state1", Constants.PERSON_COMMON);
        isUploading = false;
        PlotItem item = new PlotItem("火点", R.drawable.icon_fire);
        PlotItem item1 = new PlotItem("火线", R.drawable.icon_line);
        PlotItem item2 = new PlotItem("待定", R.drawable.icon_daiding);
        PlotItem item3 = new PlotItem("待定", R.drawable.icon_daiding);
        PlotItem item4 = new PlotItem("待定", R.drawable.icon_daiding);
        landscapeList.add(item);
        landscapeList.add(item1);
        landscapeList.add(item2);
        landscapeList.add(item3);
        landscapeList.add(item4);
        list_h_state = true;
        isTrackState = false;
        isSyncState = false;
        am1 = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_down);
        am1.setFillAfter(true);
        am1.setInterpolator(new LinearInterpolator());
        am2 = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_up);
        am2.setInterpolator(new LinearInterpolator());
        am2.setFillAfter(true);
        plotHAdapter = new PlotHorizonAdapter(getActivity(), landscapeList);
        rl_h_list.setAdapter(plotHAdapter);

        rl_h_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mGraphicOverlay.SetCurrentLayer(DEFAULT_LAYER_NAME);
                mGraphicOverlay.Attach(SecurityFragment.this, 0, DEFAULT_LAYER_NAME);
                mGraphicOverlay.SetLayerZoomWithMap(false, DEFAULT_LAYER_NAME);
                GM_EditGlobalFunc.GM_GetEgraphicGlobalPropertySetPtr().SetBoolProperty("GM_EDIT2_KEEP_CREATE", true);
                switch (position) {
                    case 0:
                        mGraphicOverlay.SelectTool(GM_TypeDefines.GM_TOOL_EDIT_CREATE, mMapView); //禁止拖动  -1  可以拖动
                        GM_PnlGlobalFunc.SetCreatingInfo(GM_TypeDefines.GM_FEATURE_TYPE_SITUATION_POINT, 2001, 47);  //火点
                        break;
                    case 1:
                        mGraphicOverlay.SelectTool(GM_TypeDefines.GM_TOOL_EDIT_CREATE, mMapView); //禁止拖动  -1  可以拖动
                        GM_PnlGlobalFunc.SetCreatingInfo(GM_TypeDefines.GM_FEATURE_TYPE_BASE_LINE, 2001, 10047);     //火线
                        break;
                    default:
                        mGraphicOverlay.SelectTool(-1, mMapView);
                        showToast(getString(R.string.undetermined_function));
                        break;
                }

                mGraphicOverlay.SetSelectable(false, SOILDERINFO_LAYER_NAME);             //
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 右侧的list
        portaitAdapter = new PortaitAdapter(getActivity(), firePlots);
        plot_listView.setOnItemClickListener(portaitAdapter);
        portaitAdapter.setOnItemSelectedListener(this);
        plot_listView.setAdapter(portaitAdapter);
        plot_listView.setOnItemLongClickListener(this);
        startBottomAnimation(list_h_state);
        locManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);
        loRaManager = (LoRaManager) getActivity().getSystemService(Context.LORA_SERVICE);
        setBDType(SpUtils.getInt(SPConstants.CHANNEL_NUM, Constants.CHANNEL_DEF));
        mBDManager = (BDManager) getActivity().getSystemService(Context.BD_SERVICE);
        if (locManager != null) {
            intLoc();
        }
        tatolMap = new HashMap<>();
        lat = SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT);
        lon = SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON);
//        radius = SpUtils.getInt(SPConstants.SECURITY_R, Constants.RADIUS);
        searchTimeArr = getActivity().getResources().getStringArray(R.array.pick_time);
        isTimerRuning = false;
        bytes = new byte[11];
        bdByteArr = null;
        isSettingEnable = false;
        syncFLId = -1;
        isShowing = false;
        sos_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sos);
        common_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_common);
        initMessageNum();
        initScrollView();
        //动态申请权限
        requestPermission();
        initDEMData();
    }


    /**
     * 主页按钮监听器
     */
    private void initMenuBtnListener() {
        mFlContainer = (FrameLayout) view.findViewById(R.id.fl_menu_container);
        LinearLayout btnDraw = (LinearLayout) view.findViewById(R.id.ll_btn_draw);
        LinearLayout btnSms= (LinearLayout) view.findViewById(R.id.ll_btn_sms);
        LinearLayout btnLocation= (LinearLayout) view.findViewById(R.id.ll_btn_location);
        LinearLayout btnSearch= (LinearLayout) view.findViewById(R.id.ll_btn_search);
        LinearLayout btnBdService= (LinearLayout) view.findViewById(R.id.ll_btn_bd_service);
        LinearLayout btnComMode= (LinearLayout) view.findViewById(R.id.ll_btn_communicate_mode);
        mIvOpen = (ImageView) view.findViewById(R.id.iv_open);
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlContainer.setVisibility(View.GONE);
                drawer.setScrimColor(Color.TRANSPARENT);
                drawer.openDrawer(Gravity.RIGHT);
            }
        });
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CommunicationListActivity.class));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    mScrollLayout.scrollToExit();
                } else {
                    mScrollLayout.setToOpen();
                    scroll_cancel.setVisibility(View.VISIBLE);
                }
            }
        });
        btnBdService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent("com.lhzw.intent.action_UPLOAD_SERVICE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnComMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow){
                    mRadioGroup.setVisibility(View.GONE);
                    mIvOpen.setImageDrawable(getResources().getDrawable(R.drawable.icon_sf_close2));
                }else {
                    mRadioGroup.setVisibility(View.VISIBLE);
                    mIvOpen.setImageDrawable(getResources().getDrawable(R.drawable.icon_sf_open2));
                }
                isShow=!isShow;
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                            //将镜头平移到当前手持机定位的中心
                            mapController = mMapView.getController();
                            mapController.setCenter(new GeoPoint(lat, lon));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
            }
        });
    }

    private void uploadItem() {
        UploadInfoBean bean = BaseUtils.getUploadInfo(firePlots.get(selectID));
        mComUtils.uploadBena(bean);
        firePlots.get(selectID).setUpload_state(Constants.UPLOAD_STATE_ON);
        CommonDBOperator.updateItem(plotDao, firePlots.get(selectID));
        portaitAdapter.notifyDataSetChanged();
        selectID = -1;
        showToast(getString(R.string.uploading_note_successful));
    }

    private void deletePlotItem() {
        CommonDBOperator.deleteItem(plotDao, firePlots.get(selectID));
        Log.e("Tag", "result code");
        if (firePlots.size() > 0) {
            deleteFirePFireL();
            firePlots.clear();
        }
        initFirePFireL(-1);

    }

    private byte[] obtainBDNum() {
        if (bdByteArr == null) {
            String numStr = mBDManager.getBDCardNumber();
            if (BaseUtils.isStringEmpty(numStr)) {
                numStr = "000000";
            }
            if (numStr.length() == 6) {
                numStr = "00" + numStr + "00";
            } else if (numStr.length() == 7) {
                numStr = "0" + numStr + "00";
            }
            bdByteArr = BaseUtils.getPerRegisterByteArr(numStr);
        }
        return bdByteArr;
    }

    private void tatolSearch() {
        mHandler.sendEmptyMessageDelayed(SEARCH_NOTE, 3000);
        tv_search_state.setText(getString(R.string.send_being));
        tv_upload_state.setText(getString(R.string.upload_state_sending));
        String[] rev = BaseUtils.formatTime(System.currentTimeMillis()).split("  ");
        tv_serach_date.setText(rev[0]);
        tv_serach_time.setText(rev[1]);
    }

    private void showSearchEndNoteDialog(int searchNum, int total, int sucess, int fail) {
        searchEndnoteDialog = new ShowSearchEndNoteDialog(getActivity());
        searchEndnoteDialog.show();
        searchEndnoteDialog.setContent(searchNum, total, sucess, fail);
        searchEndnoteDialog.setListener(this);
    }

    // 创建一个Dialog 选择定时上报时间间隔
    private void timerSearch() {
        ShowTimerDialog showTimerDialog = new ShowTimerDialog(getActivity());
        showTimerDialog.show();
        showTimerDialog.setOnTimeItemClickListener(this);
        TimerSearchAdapter adapter = new TimerSearchAdapter(getActivity(), getActivity().getResources().getStringArray(R.array.pick_time_arr));
        showTimerDialog.setAdapter(adapter);
    }

    // 自定义动画
    private void startAnimation() {
        ll_animation.setVisibility(View.VISIBLE);
        note_content_tv.setText(getString(R.string.note_switch_search));
        animation = (AnimationDrawable) im_animation.getDrawable();
        if (animation != null && !animation.isRunning()) {
            animation.start();
        }
    }

    private void stopAnimation() {
        ll_animation.setVisibility(View.GONE);
        if (animation != null && animation.isRunning())
            animation.stop();
        animation = null;
    }

    private void measureArea() {
        MeasureAreaOverlay maOverlay = (MeasureAreaOverlay) overlayManager.getOverlay(OverlayFactory.MEASUREAREA);
        if (maOverlay == null) {
            maOverlay = (MeasureAreaOverlay) OverlayFactory.createOverlayInstance(OverlayFactory.MEASUREAREA, getContext());
            mMapView.getOverlayManager().add(maOverlay);
            overlayManager.addOverlay(OverlayFactory.MEASUREAREA, maOverlay);
        }
        overlayManager.setOverlays(OverlayFactory.MEASUREAREA);
        mMapView.postInvalidate();
    }

    private void measureDEMPoint() {
        if (demAnalysis) {
            DEMPointOverlay demOverlay = (DEMPointOverlay) overlayManager.getOverlay(OverlayFactory.MEASUREDEM);
            if (demOverlay == null) {
                demOverlay = (DEMPointOverlay) OverlayFactory.createOverlayInstance(OverlayFactory.MEASUREDEM, getActivity());
                mMapView.getOverlayManager().add(demOverlay);
                overlayManager.addOverlay(OverlayFactory.MEASUREDEM, demOverlay);
            }
            overlayManager.setOverlays(OverlayFactory.MEASUREDEM);
            mMapView.postInvalidate();
        }
    }

    private void p2pViewAnalysis() {
        if (demAnalysis) {
            P2pviewAnalysisOverlay p2pOverlay = (P2pviewAnalysisOverlay) overlayManager.getOverlay(OverlayFactory.P2PVIEWANALYSIS);
            if (p2pOverlay == null) {
                p2pOverlay = (P2pviewAnalysisOverlay) OverlayFactory.createOverlayInstance(OverlayFactory.P2PVIEWANALYSIS, getActivity());
                mMapView.getOverlayManager().add(p2pOverlay);
                overlayManager.addOverlay(OverlayFactory.P2PVIEWANALYSIS, p2pOverlay);
            }
            overlayManager.setOverlays(OverlayFactory.P2PVIEWANALYSIS);
        }
    }

    //获取分析结果
    private List<SectionChartPoint> getP2pAnalysisPoints() {
        P2pviewAnalysisOverlay p2pOverlay = (P2pviewAnalysisOverlay) overlayManager.getOverlay(OverlayFactory.P2PVIEWANALYSIS);
        if (p2pOverlay != null) {
            List<SectionChartPoint> points = p2pOverlay.getPoints();
            Collections.sort(points);
            return points;
        }
        return null;
    }

    private void measureDistance() {
        MeasureDistanceOverlay mdOverlay = (MeasureDistanceOverlay) overlayManager.getOverlay(OverlayFactory.MEASUREDISTANCE);
        if (mdOverlay == null) {
            mdOverlay = (MeasureDistanceOverlay) OverlayFactory.createOverlayInstance(OverlayFactory.MEASUREDISTANCE, getContext());
            mMapView.getOverlayManager().add(mdOverlay);
            overlayManager.addOverlay(OverlayFactory.MEASUREDISTANCE, mdOverlay);
        }
        overlayManager.setOverlays(OverlayFactory.MEASUREDISTANCE);
        mMapView.postInvalidate();
    }

    private void startBottomAnimation(boolean state) {
        if (state) {
            rl_h_list.startAnimation(am1);
            rl_h_list.setVisibility(View.GONE);
            rl_h_list.clearAnimation();
            rl_h_list.invalidate();
        } else {
            rl_h_list.startAnimation(am2);
            rl_h_list.setVisibility(View.VISIBLE);
        }
        list_h_state = !list_h_state;
    }

    private void startLeftAnimation(boolean state) {
        if (state) {
            Animation am = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_dimss);
            am.setInterpolator(new LinearInterpolator());
            ll_plot_tools.startAnimation(am);
            ll_plot_tools.setVisibility(View.GONE);
        } else {
            Animation am = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_out);
            am.setInterpolator(new LinearInterpolator());
            ll_plot_tools.startAnimation(am);
            ll_plot_tools.setVisibility(View.VISIBLE);
        }
    }

    private void startLeftTrackAnimation(boolean state) {
        if (state) {
            Animation am = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_dimss);
            am.setInterpolator(new LinearInterpolator());
            bt_track_complete.startAnimation(am);
            bt_track_complete.setVisibility(View.GONE);
            bt_track_complete.clearAnimation();
            bt_track_complete.invalidate();
        } else {
            Animation am = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_out);
            am.setInterpolator(new LinearInterpolator());
            bt_track_complete.startAnimation(am);
            bt_track_complete.setVisibility(View.VISIBLE);
        }
    }

    private void startPlotLeftAnimation(boolean state) {
        if (state) {
            Animation am = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_dimss);
            am.setInterpolator(new LinearInterpolator());
            ll_plot_function.startAnimation(am);
            ll_plot_function.setVisibility(View.GONE);
            ll_plot_function.clearAnimation();
            ll_plot_function.invalidate();
            Log.e("Tag", "startPlotLeftAnimation");
        } else {
            Animation am = AnimationUtils.loadAnimation(getActivity(), R.anim.anima_out);
            am.setInterpolator(new LinearInterpolator());
            ll_plot_function.startAnimation(am);
            ll_plot_function.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocation() {
        try {
            if (locManager != null) {
                String bestProvider = locManager.getBestProvider(criteria, true);
                locManager.requestLocationUpdates(bestProvider, 20000, 0, this);
                isCurrentApp = true;
                isFisre = true;
                mHandler.sendEmptyMessageDelayed(GPS_FAIL, 60000);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Tag", "criteria is null : " + e.getMessage());
        }
    }


    private void intLoc() {
        // 北斗定位
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GPS_FAIL:// 主线程改UI
                    isFisre = true;
                    showToast(getString(R.string.note_location_fail));
                    mHandler.sendEmptyMessageDelayed(GPS_FAIL, 60000);
                    break;
                case TIMER_REPORT:
                    //上报搜索信息
                    if (!isUpload) {
                        return;
                    }
                    List<PersonalInfo> rxList = CommonDBOperator.queryByOrderKey(persondao, "num");
                    if (rxList != null && rxList.size() > 0) {
                        Log.e("Tag", "size : " + rxList.size());
                        String body = "";
                        String locTime = "";
                        String register = "";
                        String offset = "";
                        int counter = 0;
                        String local_latlon = SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT) + "," + SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON);
                        List<UploadInfoBean> uploadList = new ArrayList<>();
                        for (PersonalInfo item : rxList) {
                            if (counter == rxList.size() - 1) {
                                body += item.getLatitude() + "," + item.getLongitude();
                                locTime += item.getLocTime();
                                offset += counter;
                                register += item.getNum();
                            } else {
                                body += item.getLatitude() + "," + item.getLongitude() + "-";
                                locTime += item.getLocTime() + "-";
                                offset += counter + "-";
                                register += item.getNum() + "-";
                            }
                            counter++;
                        }
                        UploadInfoBean bean = new UploadInfoBean(Constants.TX_JZH, Constants.TX_COMMON, System.currentTimeMillis(), body, locTime, offset, local_latlon,
                                SpUtils.getLong(SPConstants.LOC_TIME, System.currentTimeMillis()), 0, SpUtils.getString(Constants.UPLOAD_JZH_NUM, Constants.BD_NUM_DEF), 0, isRuuning ? BaseUtils.getSendID() : -1, register);
                        uploadList.add(bean);
                        if (SpUtils.getInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_0) == Constants.UOLOAD_STATE_1) {
                            bean.setTx_type(Constants.TX_QZH);
                            bean.setNum(SpUtils.getString(Constants.UPLOAD_QZH_NUM, Constants.BD_NUM_DEF));
                            uploadList.add(bean);
                        }
                        rxList.clear();
                        mComUtils.uploadBena(uploadList);
                        // 写入日志
                        try {
                            LogWrite writer = LogWrite.open();
                            String log = LogWrite.df.format(System.currentTimeMillis()) + " \t data_type = " + Constants.TX_COMMON + " latLons : " + body;
                            writer.writeLog(log);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        List<UploadInfoBean> uploadList = new ArrayList<>();
                        String local_latlon = SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT) + "," + SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON);
                        UploadInfoBean bean = new UploadInfoBean(Constants.TX_JZH, Constants.TX_COMMON, System.currentTimeMillis(), null, null, null, local_latlon,
                                SpUtils.getLong(SPConstants.LOC_TIME, System.currentTimeMillis()), 0, SpUtils.getString(Constants.UPLOAD_JZH_NUM, Constants.BD_NUM_DEF), 0, isRuuning ? BaseUtils.getSendID() : -1, null);
                        uploadList.add(bean);
                        mComUtils.uploadBena(uploadList);
                    }

                    tv_search_state.setText(getString(R.string.upload_data));
                    tv_upload_state.setText(getString(R.string.upload_state_waiting));
                    String[] rev = BaseUtils.formatTime(System.currentTimeMillis()).split("  ");
                    tv_serach_date.setText(rev[0]);
                    tv_serach_time.setText(rev[1]);
                    mHandler.sendEmptyMessageDelayed(COMPLETE, 2000);
                    break;
                case SYNC_DELAY_SIGNAL:
                    //延时
                    try {
                        showToast(getString(R.string.sync_fire_line_fail));
                        isSyncState = false;
                        tv_search_note.setText("");
                        tv_search_note.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SEARCH:
//                    cleanDatabase();
                    mHandler.sendEmptyMessageDelayed(TIMER_REPORT, 60000);  //定时延时后，在延时10秒上报搜索信息
                    mHandler.sendEmptyMessageDelayed(SEARCH, delay);
                    break;
                case SEARCH_DELAY_CLOSE:
//                    SpUtils.putBoolean(SPConstants.COMMON_SWITCH, false);
                    break;
                case TOTAL_SEARCH_DELAY:
                    try {
                        int counter = (int) msg.obj;
//                        SpUtils.putBoolean(SPConstants.COMMON_SWITCH, false);//关闭搜索
                        if (total == 0) {
                            showToast(getString(R.string.total_search_person_none));
                            return;
                        }
                        showSearchEndNoteDialog(counter, total, sucess, fail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TOATAL_UPLOADING:
                    try {
                        showSearchToast(getString(R.string.uploading_bd_data));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TOATAL_UPLOADED:
                    try {
                        isUploading = false;
                        tv_signal_search_note.setVisibility(View.GONE);
                        tv_signal_search_note.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ZOOM_DIMISS:
                    ll_zoom_tools.setVisibility(View.GONE);
                    break;
                case SEARCH_NOTE:
                    if (isUpload) {
                        tv_search_state.setText(getString(R.string.send_success));
                        tv_upload_state.setText(getString(R.string.upload_state_sent));
                        String[] rev1 = BaseUtils.formatTime(System.currentTimeMillis()).split("  ");
                        tv_serach_date.setText(rev1[0]);
                        tv_serach_time.setText(rev1[1]);
                        mHandler.sendEmptyMessageDelayed(TIMER_REPORT, 2000);
                        //保存手表数据
                        saveWactchTime();
                    }
                    break;
                case TREE_REFLESH:
                    treeDialog.refleshView(content);
                    break;
                case REFLESH_TV:
                    tv_search_state.setText(getString(R.string.searching_note).replace("@", msg.obj + ""));
                    String[] rev1 = BaseUtils.formatTime(System.currentTimeMillis()).split("  ");
                    tv_serach_date.setText(rev1[0]);
                    tv_serach_time.setText(rev1[1]);
                    break;
                case COMPLETE:
                    BaseUtils.flipAnimatorXViewShow(rl_upload_state_progress, rl_upload_outer, 200);
                    scanani_view.stopAnimation();
                    tv_search_state.setText("");
                    tv_serach_date.setText("");
                    tv_serach_time.setText("");
                    tv_update_leisure.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void saveWactchTime() {
        CommonDBOperator.deleteAllItems(locTimeDao);
        List<WatchLastLocTime> watchLastLoc = new ArrayList<>();
        for (PersonalInfo bean : sosList) {
            WatchLastLocTime item = new WatchLastLocTime(bean.getNum(), bean.getLocTime());
            watchLastLoc.add(item);
        }
        for (PersonalInfo bean : commonList) {
            WatchLastLocTime item = new WatchLastLocTime(bean.getNum(), bean.getLocTime());
            watchLastLoc.add(item);
        }
        for (PersonalInfo bean : undetermined_List) {
            WatchLastLocTime item = new WatchLastLocTime(bean.getNum(), bean.getLocTime());
            watchLastLoc.add(item);
        }
        CommonDBOperator.saveToDBBatch(locTimeDao, watchLastLoc);
        watchLastLoc.clear();
        mScrollAdapter.refleshView();
    }


    @Override
    public void Update(int iMessage, Object oValue, Object pValue) {
        if (!ll_zoom_tools.isShown()) {
            ll_zoom_tools.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(ZOOM_DIMISS, 3000);
        } else {
            mHandler.removeMessages(ZOOM_DIMISS);
            mHandler.sendEmptyMessageDelayed(ZOOM_DIMISS, 3000);
        }
        if (iMessage == GM_MESSAGE_ENGINE.GM_MESSAGE_EDIT_CREATE_END) {
            String szLayerName = (String) pValue;
            int iID = (Integer) oValue;
            int itype = mGraphicOverlay.GetFeatureType(iID, szLayerName);

            if (itype == GM_TypeDefines.GM_FEATURE_TYPE_SITUATION_POINT) {
                //点军标
                int iLibID[] = {0};
                int iSymbolCode[] = {0};
                mGraphicOverlay.GetSituationPointCode(iID, iLibID, iSymbolCode, szLayerName);

                if (iLibID[0] == 2001 && iSymbolCode[0] == 47) {  // 火点
                    //火点
                    GeoPoint dpt_pos = new GeoPoint(0, 0);
                    mGraphicOverlay.GetSituationPointPos(iID, dpt_pos, szLayerName);
                    ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
                    list.add(dpt_pos);
                    PlotItemInfo plot = new PlotItemInfo("", iID, System.currentTimeMillis(), BaseUtils.geoPointToString(list), Constants.TX_FIREPOIT, 0, Constants.TX_JZH, Constants.UPLOAD_STATE_OFF);
                    newFirePlots.add(plot);
                    // 释放数据
                    list.clear();
                    // 更新列表
                    portaitAdapter.initPlotItem();
                    portaitAdapter.notifyDataSetChanged();
                }
            } else if (itype == GM_TypeDefines.GM_FEATURE_TYPE_BASE_LINE) {  // 火线
                int iLineSymID = ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(szLayerName).GetFeatureByID(iID)).GetLineSymbolID();
                Log.e("Tag", "draw line ID = " + iLineSymID);
                if (iLineSymID == 2001010047) {
                    //火线
                    Vector<GeoPoint> ptsReturn = new Vector<GeoPoint>();
                    mGraphicOverlay.GetPolylinePoints(iID, ptsReturn, szLayerName);
                    List<GeoPoint> gps = new ArrayList<>(ptsReturn);
                    Log.e("Tag", "draw line len = " + gps.size());
                    if (gps.size() >= 2) {
                        PlotItemInfo plot = new PlotItemInfo("", iID, System.currentTimeMillis(), BaseUtils.geoPointToString(gps), Constants.TX_FIRELINE, 0, Constants.TX_JZH, Constants.UPLOAD_STATE_OFF);
                        newFirePlots.add(plot);
                        portaitAdapter.initPlotItem();
                        portaitAdapter.notifyDataSetChanged();
                        // 清空数据
                        gps.clear();
                    }
                }
            }
        }
        if (iMessage == GM_MESSAGE_ENGINE.GM_MESSAGE_SELECTION_CHANGED) {//
            final IGM_GraphicLayer pGraphicLayer = (IGM_GraphicLayer) pValue;
            String layerName = pGraphicLayer.GetName();
            if (layerName.equals(DEFAULT_LAYER_NAME)) {//点击火点火线
                int iCount = pGraphicLayer.GetSelectionPtr().GetFeatureCount();
                if (iCount > 0) {
                    int selectedIds[] = new int[iCount];
                    for (int i = 0; i < iCount; i++) {
                        int id = pGraphicLayer.GetSelectionPtr().GetFeatureID(i);
                        selectedIds[i] = id;
                    }
                }

            } else if (layerName.equals(SOILDERINFO_LAYER_NAME)) {//点击sos
                int iCount = pGraphicLayer.GetSelectionPtr().GetFeatureCount();
                if (iCount > 0) { //如果选中了标号
                    for (int i = 0; i < iCount; i++) {
                        int touchId = pGraphicLayer.GetSelectionPtr().GetFeatureID(i);//获取第一个点击的标号的id
                        IGT_PropertySet pProp = pGraphicLayer.GetFeatureByID(touchId).GetPropertyPtr();
                        GT_Field field = pProp.GetPropertyPtr("String_PROP");
                        if (field != null) {
                            if (field.GetString().trim().equals(Constants.PERSON_SOS)) {
                                //sos的点击事件
                                codeArr = BaseUtils.getPerRegisterByteArr(tatolMap.get(touchId).getNum());
                                byte[] numByte = obtainBDNum();
                                for (int j = 0; j < 4; j++) {
                                    codeArr[5 + j] = numByte[j];
                                }
                                showSosPopupWindow(tatolMap.get(touchId));
                                icon_id = touchId;
                                pGraphicLayer.GetSelectionPtr().RemoveFeatureByID(icon_id);
                                setTouched(touchId);
                            } else if (field.GetString().trim().equals(Constants.PERSON_COMMON)) {
                                //common的点击事件
                                icon_id = touchId;
                                showNormalPopupWindow(tatolMap.get(touchId));
                                pGraphicLayer.GetSelectionPtr().RemoveFeatureByID(icon_id);
                                setTouched(touchId);
                            }
                        }
                    }
                } else {
                    setTouched(-1);
                }
            }
        }
    }

    //点击变大
    private void setTouched(int id) {
        if(isShow){
            mRadioGroup.setVisibility(View.GONE);
            isShow=!isShow;
           // mIvSwitchRg.setImageDrawable(getResources().getDrawable(R.drawable.icon_open));
            mIvOpen.setImageDrawable(getResources().getDrawable(R.drawable.icon_sf_close2));

        }
        for (Map.Entry<Integer, PersonalInfo> entry : tatolMap.entrySet()) {
            if (entry.getKey() == id) {
                if (mGraphicOverlay != null) {
                    mGraphicOverlay.SetFeatureProp(entry.getKey(), PROP_NAME.Width, "30.0", SOILDERINFO_LAYER_NAME);
                }
            } else {
                if (mGraphicOverlay != null) {
                    mGraphicOverlay.SetFeatureProp(entry.getKey(), PROP_NAME.Width, "20.0", SOILDERINFO_LAYER_NAME);
                }
            }
        }
        refreshMap();
    }

    // 显示SOS的popwindow
    private void showSosPopupWindow(PersonalInfo item) {
        if (isShowing) return;
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popup_sos, null);
        mPopWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 获取屏幕
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mPopWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(576);
        mPopWindow.setContentView(contentView);
        mPopWindow.setOnDismissListener(this);
        Log.d("height", display.getHeight() * 3 / 10 + "");
        TextView pop_name_tv = (TextView) contentView
                .findViewById(R.id.pop_name_tv);
        pop_name_tv.setText(item.getName());
        TextView pop_sex_tv = (TextView) contentView
                .findViewById(R.id.pop_sex_tv);
        pop_sex_tv.setText(item.getSex());
        TextView pop_phone_tv = (TextView) contentView
                .findViewById(R.id.pop_phone_tv);
        pop_phone_tv.setText(item.getPhone());
        TextView pop_urgency_phone_tv = (TextView) contentView
                .findViewById(R.id.pop_urgency_phone_tv);
        pop_urgency_phone_tv.setText(item.getContact1());
        TextView pop_urgency_phone_tv2 = (TextView) contentView
                .findViewById(R.id.pop_urgency_phone_tv2);
        pop_urgency_phone_tv2.setText(item.getContact2());
        ImageView distance_bt = (ImageView) contentView
                .findViewById(R.id.distanceinfer);
        distance_bt.setOnClickListener(this);
        TextView distance_tv = (TextView) contentView
                .findViewById(R.id.distancetv);

        distance_tv.setText("距离您：" + df.format(GT_GeoArithmetic.ComputeDistanceOfTwoPoints(new GeoPoint(Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())),
                new GeoPoint(lat, lon))) + "m");

        LinearLayout pop_track_bt = (LinearLayout) contentView
                .findViewById(R.id.pop_track_bt);
        pop_track_bt.setBackgroundColor(getResources().getColor(
                R.color.gray_background));
        LinearLayout pop_rescue_bt = (LinearLayout) contentView
                .findViewById(R.id.pop_rescue_bt);
//		pop_track_bt.setOnClickListener(this);
        pop_rescue_bt.setOnClickListener(this);
        // 显示 PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(
                R.layout.security_fg, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        isShowing = true;
    }

    // 显示normal的popwindow
    private void showNormalPopupWindow(final PersonalInfo item) {
        if (isShowing) return;
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popup_normal_command, null);
        mPopWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 获取屏幕
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mPopWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(576);
        mPopWindow.setContentView(contentView);
        mPopWindow.setOnDismissListener(this);
        Log.d("height", display.getHeight() * 3 / 10 + "");
        // 显示
        TextView pop_name_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_name_tv);
        pop_name_tv.setText(item.getName());

        TextView pop_sex_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_sex_tv);
        pop_sex_tv.setText(item.getSex());

        TextView pop_phone_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_phone_tv);
        pop_phone_tv.setText(item.getPhone());
        TextView pop_urgency_phone_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_urgency_phone_tv);
        pop_urgency_phone_tv.setText(item.getContact1());

        TextView pop_urgency_phone_tv2 = (TextView) contentView
                .findViewById(R.id.normal_pop_urgency_phone_tv2);
        pop_urgency_phone_tv2.setText(item.getContact2());

        TextView distance_tv = (TextView) contentView
                .findViewById(R.id.normal_distancetv);

        distance_tv.setText("距离您：" + df.format(GT_GeoArithmetic.ComputeDistanceOfTwoPoints(new GeoPoint(Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())),
                new GeoPoint(lat, lon))) + "m");
        TextView tv_issue_command = (TextView) contentView.findViewById(R.id.tv_issue_command);
        tv_issue_command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                ShowAlertDialogCommand(BaseUtils.getPerRegisterByteArr(item.getNum()));
            }
        });
        // 显示 PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(
                R.layout.security_fg, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        isShowing = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isCurrentApp && !BaseUtils.outOfChina(location.getLatitude(), location.getLongitude())) {
            // 保存定位信息
            GeoPoint gPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            double distance = GT_GeoArithmetic.ComputeDistanceOfTwoPoints(gPoint,
                    new GeoPoint(lat, lon));
            SpUtils.putLong(SPConstants.LOC_TIME, System.currentTimeMillis());
            if (distance > 10) {
                //更新地图手持机的相对位置
                lon = location.getLongitude();
                lat = location.getLatitude();
                SpUtils.putFloat(SPConstants.LON_ADDR, (float) location.getLongitude());
                SpUtils.putFloat(SPConstants.LAT_ADDR, (float) location.getLatitude());
                //刷新手持机位置
                if (isTrackState) {
                    trackList.add(new GeoPoint(lat, lon));
                    if (trackList.size() > 2) { //两点才能有路径
                        drawTrackLine(BaseUtils.listToGeoPointArr(trackList));
                    }
                }
                createMarker(gPoint);
                if (isFisre) {
                    isFisre = false;
                    showToast(getString(R.string.location_success));
                }
            }
            mHandler.removeMessages(GPS_FAIL);
            mHandler.sendEmptyMessageDelayed(GPS_FAIL, 60000);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void createMarker(GeoPoint geoPoint) {
        if (isShowing) return;
        if (locationOverlay != null) {
            mMapView.getOverlayManager().remove(locationOverlay);
        }
        final ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem item = new OverlayItem("", "", geoPoint);
        Drawable marker = getResources().getDrawable(R.drawable.location_icon, null);
        item.setMarker(marker);
        items.add(item);
        locationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

            @Override
            public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {
                ShowAlertDialogCommand(bytes);
                dialogCommmand.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isShowing = false;
                    }
                });
                isShowing = true;
                return false;
            }


            @Override
            public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                return false;
            }
        }, new DefaultResourceProxyImpl(getActivity()));
        locationOverlay.setFocusItemsOnTap(true);
        locationOverlay.setFocusedItem(0);
        mMapView.getOverlayManager().add(locationOverlay);
//        mGraphicOverlay.setModified();
        mMapView.postInvalidate();
    }
    /*
    private void addLcoationLayer(GeoPoint gp, double radius) {
        localFeature_id = mGraphicOverlay.AddCircle(gp, radius, Color.parseColor("#0984f3"), LOCATION_LAYER_NAME);
        mGraphicOverlay.SetFeatureProp(localFeature_id, PROP_NAME.FillType, "SolidGradient", LOCATION_LAYER_NAME);
        mGraphicOverlay.SetFeatureProp(localFeature_id, PROP_NAME.SolidFillColor, "" + Color.parseColor("#b6cbdd"), LOCATION_LAYER_NAME);
        mGraphicOverlay.SetFeatureProp(localFeature_id, PROP_NAME.SolidFillOpacity, "40", LOCATION_LAYER_NAME);
        mGraphicOverlay.SetLayerZoomWithMap(true, LOCATION_LAYER_NAME);
        createMarker(gp);
        createMarker(gp);
    }
    */

    //修改当前位置为中心的半径大小
    private void setLocationRange(int id, double radius) {
        mGraphicOverlay.SetCircleRadius(id, radius, LOCATION_LAYER_NAME);
        mGraphicOverlay.setModified();
//        mMapView.postInvalidate();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFisre = true;
        isCurrentApp = false;
        mHandler.removeMessages(GPS_FAIL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try {
            if (locManager != null && locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locManager.removeUpdates(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int pos) {
        //点击事件处理
        Log.e("Tag", "pos : " + pos);
        if ((Constants.PERSON_COMMON).equals(undetermined_List.get(pos)
                .getState1())) {
            showUnderlineNormalPopupWindow(pos);
        } else if ((Constants.PERSON_SOS).equals(undetermined_List.get(pos)
                .getState1())) {
            codeArr = BaseUtils.getPerRegisterByteArr(undetermined_List
                    .get(pos).getNum());
            byte[] numByte = obtainBDNum();
            for (int i = 0; i < 4; i++) {
                codeArr[5 + i] = numByte[i];
            }
            showUnderlineSosPopupWindow(pos);
        } else if ((Constants.PERSON_OFFLINE).equals(undetermined_List.get(pos)
                .getState())) {
            showUnderlineOfflinePopupWindow(pos);
        }
    }

    @Override
    public void onTimerClick(final long pos) {
        // 定时搜索
        Log.e("Tag", "pos : " + searchTimeArr[(int) pos]);
        if (!isTimerRuning) {
            isTimerRuning = true;
            startAnimation();
//            SpUtils.putBoolean(SPConstants.COMMON_SWITCH, true);
            delay = Integer.valueOf(searchTimeArr[(int) pos]) * 60 * 1000;
            showSearchToast(getString(R.string.timer_search_start_note));
            mHandler.sendEmptyMessage(SEARCH);
            rl_upload_inner.setEnabled(true);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("Tag", "pos : " + position);
        selectID = position;
        showHandleDialog();
        if (firePlots.get(position).getUpload_state() == Constants.UPLOAD_STATE_ON) {
            handleDialog.setUploadNote();
        }
        return false;
    }

    @Override
    public void onCancel() {
        isUploading = false;
        isRuuning = false;
//        SpUtils.putBoolean(SPConstants.COMMON_SWITCH, false);
        if (isUpload) {
            //保存数据
            CommonDBOperator.saveToDB(treeDao, uploadLocTimeBean);
            BaseUtils.increaseID();
            isUpload = false;
            //更新数据库
            CommonDBOperator.deleteAllItems(locTimeDao);
            List<WatchLastLocTime> watchLastLoc = new ArrayList<>();
            for (PersonalInfo bean : sosList) {
                WatchLastLocTime item = new WatchLastLocTime(bean.getNum(), bean.getLocTime());
                watchLastLoc.add(item);
            }
            for (PersonalInfo bean : commonList) {
                WatchLastLocTime item = new WatchLastLocTime(bean.getNum(), bean.getLocTime());
                watchLastLoc.add(item);
            }
            for (PersonalInfo bean : undetermined_List) {
                WatchLastLocTime item = new WatchLastLocTime(bean.getNum(), bean.getLocTime());
                watchLastLoc.add(item);
            }
            CommonDBOperator.saveToDBBatch(locTimeDao, watchLastLoc);
            watchLastLoc.clear();
        }
        content = new String[]{"", "", "", "", "", "", "", "", "", ""};
        treeDialog = null;
    }

    @Override
    public boolean onScroll(ScrollEvent scrollEvent) {
        String NS = "";
        String EW = "";
        GeoPoint geo = getScreenCenter();
        double lat = geo.getLatitudeE6() / 1000000.0d;
        double lon = geo.getLongitudeE6() / 1000000.0d;
        if (lat > 0 && lat <= 180) {
            EW = "E";
        } else {
            EW = "W";
        }

        if (lon > 0 && lon <= 90) {
            NS = "S";
        } else {
            NS = "N";
        }
        tv_location.setText(getString(R.string.location_lon) + BaseUtils.doubleToDegree(Math.abs(lon)) + EW + ", " + getString(R.string.location_lat) + BaseUtils.doubleToDegree(Math.abs(lat)) + NS);
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent zoomEvent) {
        return false;
    }

    @Override
    public boolean onLongPress(LongPressEvent longPressEvent) {
        return false;
    }

    @Override
    public boolean onTouch(TouchEvent touchEvent) {
        if (isOpen) {
            mScrollLayout.setToExit();
            mScrollLayout.getBackground().setAlpha(0);
        }
        return false;
    }

    private GeoPoint getScreenCenter() {
        MapView.Projection proj = mMapView.getProjection();
        Point mPoint = new Point();
        proj.fromScreenCoordsToMapPixels(mMapView.getWidth() / 2, mMapView.getHeight() / 2,
                mPoint);
        GeoPoint coordinate = (GeoPoint) proj.CGCS2000fromPixels(mPoint.x, mPoint.y);
        return coordinate;
    }

    @Override
    public void onSrollItemClick(String state, int pos) {
        if (state.equals(Constants.PERSON_SOS)) {
            mapController = mMapView.getController();
            mapController.setCenter(new GeoPoint(Double.valueOf(sosList.get(pos).getLatitude()), Double.valueOf(sosList.get(pos).getLongitude())));
        } else if (state.equals(Constants.PERSON_COMMON)) {
            mapController = mMapView.getController();
            mapController.setCenter(new GeoPoint(Double.valueOf(commonList.get(pos).getLatitude()), Double.valueOf(commonList.get(pos).getLongitude())));
        } else if (state.equals(Constants.PERSON_UNDETERMINED)) {
            //点击事件处理
            Log.e("Tag", "pos : " + pos);
            if ((Constants.PERSON_COMMON).equals(undetermined_List.get(pos)
                    .getState1())) {
                showUnderlineNormalPopupWindow(pos);
            } else if ((Constants.PERSON_SOS).equals(undetermined_List.get(pos)
                    .getState1())) {
                codeArr = BaseUtils.getPerRegisterByteArr(undetermined_List
                        .get(pos).getNum());
                byte[] numByte = obtainBDNum();
                for (int i = 0; i < 4; i++) {
                    codeArr[5 + i] = numByte[i];
                }
                showUnderlineSosPopupWindow(pos);
            } else if ((Constants.PERSON_OFFLINE).equals(undetermined_List.get(pos)
                    .getState())) {
                showUnderlineOfflinePopupWindow(pos);
            }
        }
        if (isOpen) {
            mScrollLayout.setToExit();
            mScrollLayout.getBackground().setAlpha(0);
        }
    }

    private class Action implements Runnable {
        @Override
        public void run() {
            if (isTimerRuning) {
                List<PersonalInfo> list = null;
                boolean isUpload = false;
                byte[] numByte = obtainBDNum();
                for (int counter = 0; counter < 3; counter++) {
                    list = CommonDBOperator.queryByKeys(persondao, "state", Constants.PERSON_OFFLINE + "");
                    Message msg = mHandler.obtainMessage(REFLESH_TV);
                    msg.obj = counter + 1;
                    mHandler.sendMessage(msg);
                    if (counter == 0) {
                        isUpload = list.size() > 0 ? true : false;
                    }
                    for (PersonalInfo item : list) {
                        if (!isTimerRuning) {
                            return;
                        }
                        byte[] sndByte = BaseUtils.getPerRegisterByteArr(item.getNum());

                        for (int pos = 0; pos < 4; pos++) {
                            sndByte[5 + pos] = numByte[pos];
                        }
                        sendCMDSearch(sndByte);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                isTimerRuning = false;
                mHandler.sendEmptyMessage(COMPLETE);
            }
        }
    }


    /**
     * 将标绘插入数据库
     */
    private void insertPlotsToDataBase(List<PlotItemInfo> plotItemInfos) {
        for (PlotItemInfo plotItemInfo : plotItemInfos) {
            Log.e("Tag", "insert database  fireL");
            CommonDBOperator.saveToDB(plotItemDao, plotItemInfo);
        }
    }

    /**
     * 搜救删除数据库
     */
    private void cleanDatabase() {
        List<PersonalInfo> list = CommonDBOperator.getList(persondao);
        for (PersonalInfo item : list) {
            item.setState1("");
            item.setState(Constants.PERSON_OFFLINE);
            item.setTime(System.currentTimeMillis());
            CommonDBOperator.updateItem(persondao, item);
        }
        initSoilderInfoList();
    }

    private void updataState(int pos) {
        try {
            String num = tatolMap.get(pos).getNum();
            List<PersonalInfo> list = CommonDBOperator.queryByKeys(persondao,
                    "num", num);
            if (list != null && list.size() > 0) {
                list.get(0).setState(Constants.PERSON_COMMON);
                list.get(0).setTime(System.currentTimeMillis());
                CommonDBOperator.updateItem(persondao, list.get(0));
                initSoilderInfoList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 显示中心点的popup
    private void ShowAlertDialogCommand(final byte[] sndBytes) {
        dialogCommmand = new ShowAlertDialogCommand(getActivity());
        dialogCommmand.showDialog();
        dialogCommmand.setListener(this);
        dialogCommmand.setAdapter(new CommandAdapter(getActivity()));
        dialogCommmand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                byte[] numByte1 = obtainBDNum();
                for (int j = 0; j < 4; j++) {
                    sndBytes[5 + j] = numByte1[j];
                }
                position++;
                sendCMDAction(sndBytes, (byte) (position & 0xff));

                for (int pos = 0; pos < sndBytes.length; pos++) {
                    Log.e("Tag", "sndBytes[" + pos + "] = " + Integer.toHexString(sndBytes[pos]));
                }
                dialogCommmand.clear();
                showToast(getString(R.string.send_command_success_note));
                //TODO 发指令  就清空上次的指令  更新指令表的状态
                Dao<PersonalInfo, Integer> infoDao = helper.getPersonalInfoDao();
                List<PersonalInfo> infoList = CommonDBOperator.getList(infoDao);
                if (infoList != null && infoList.size() > 0) {
                    for (int i = 0; i < infoList.size(); i++) {
                        infoList.get(i).setFeedback(0);//重置为未确认
                        CommonDBOperator.updateItem(infoDao, infoList.get(i));
                    }
                }

            }
        });
    }

    private void sendCMDAction(final byte[] num, final byte cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loRaManager.sendActionCmd(num, (byte) cmd);
                Log.e("Tag", "sendCMDAction  action  " + cmd + "  num  " + num);
            }
        }).start();
    }

    private boolean sendCMDSearch(final byte[] num) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                loRaManager.searchCard(num);
            }
        }).start();
        return true;
    }

    // 待定区域普通人员
    private void showUnderlineNormalPopupWindow(final int pos) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_normal_command, null);
        mPopWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 获取屏幕
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mPopWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(576);
        mPopWindow.setContentView(contentView);
        mPopWindow.setOnDismissListener(this);
        Log.d("height", display.getHeight() * 3 / 10 + "");
        final PersonalInfo item = undetermined_List.get(pos);
        // 显示
        TextView pop_name_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_name_tv);
        pop_name_tv.setText(item.getName());

        TextView pop_sex_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_sex_tv);
        pop_sex_tv.setText(item.getSex());

        TextView pop_phone_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_phone_tv);
        pop_phone_tv.setText(item.getPhone());
        TextView pop_urgency_phone_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_urgency_phone_tv);
        pop_urgency_phone_tv.setText(item.getContact1());

        TextView pop_urgency_phone_tv2 = (TextView) contentView
                .findViewById(R.id.normal_pop_urgency_phone_tv2);
        pop_urgency_phone_tv2.setText(item.getContact2());
        TextView distance_tv = (TextView) contentView
                .findViewById(R.id.normal_distancetv);
        distance_tv.setText("距离您：0m");
        TextView tv_issue_command = (TextView) contentView.findViewById(R.id.tv_issue_command);
        tv_issue_command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                ShowAlertDialogCommand(BaseUtils.getPerRegisterByteArr(item.getNum()));
            }
        });
        // 显示 PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(
                R.layout.security_fg, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    // 待定区域普通人员
    private void showUnderlineOfflinePopupWindow(int pos) {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popup_normal_command, null);
        mPopWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 获取屏幕
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mPopWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(576);
        mPopWindow.setContentView(contentView);
        mPopWindow.setOnDismissListener(this);
        Log.d("height", display.getHeight() * 3 / 10 + "");
        PersonalInfo item = undetermined_List.get(pos);
        // 显示
        TextView pop_name_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_name_tv);
        pop_name_tv.setText(item.getName());

        TextView pop_sex_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_sex_tv);
        pop_sex_tv.setText(item.getSex());

        TextView pop_phone_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_phone_tv);
        pop_phone_tv.setText(item.getPhone());
        TextView pop_urgency_phone_tv = (TextView) contentView
                .findViewById(R.id.normal_pop_urgency_phone_tv);
        pop_urgency_phone_tv.setText(item.getContact1());

        TextView pop_urgency_phone_tv2 = (TextView) contentView
                .findViewById(R.id.normal_pop_urgency_phone_tv2);
        pop_urgency_phone_tv2.setText(item.getContact2());
        TextView distance_tv = (TextView) contentView
                .findViewById(R.id.normal_distancetv);
        distance_tv.setText("距离您：" + "0" + "m");
        TextView tv_issue_command = (TextView) contentView.findViewById(R.id.tv_issue_command);
        tv_issue_command.setBackgroundColor(getResources().getColor(R.color.gray_background));
        // 显示 PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(
                R.layout.security_fg, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    // 待定区域sos
    private void showUnderlineSosPopupWindow(int pos) {

        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popup_sos, null);
        mPopWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 获取屏幕
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mPopWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(576);
        mPopWindow.setContentView(contentView);
        mPopWindow.setOnDismissListener(this);
        Log.d("height", display.getHeight() * 3 / 10 + "");
        final PersonalInfo item = undetermined_List.get(pos);

        TextView pop_name_tv = (TextView) contentView
                .findViewById(R.id.pop_name_tv);
        pop_name_tv.setText(item.getName());
        TextView pop_sex_tv = (TextView) contentView
                .findViewById(R.id.pop_sex_tv);
        pop_sex_tv.setText(item.getSex());
        TextView pop_phone_tv = (TextView) contentView
                .findViewById(R.id.pop_phone_tv);
        pop_phone_tv.setText(item.getPhone());
        TextView pop_urgency_phone_tv = (TextView) contentView
                .findViewById(R.id.pop_urgency_phone_tv);
        pop_urgency_phone_tv.setText(item.getContact1());
        TextView pop_urgency_phone_tv2 = (TextView) contentView
                .findViewById(R.id.pop_urgency_phone_tv2);
        pop_urgency_phone_tv2.setText(item.getContact2());
        ImageView distance_bt = (ImageView) contentView
                .findViewById(R.id.distanceinfer);
        distance_bt.setBackgroundResource(R.drawable.daohang_enable2);
        // distance_bt.setOnClickListener(this);
        TextView distance_tv = (TextView) contentView
                .findViewById(R.id.distancetv);

        distance_tv.setText("距离您：" + "0m");

        LinearLayout pop_track_bt = (LinearLayout) contentView
                .findViewById(R.id.pop_track_bt);
        pop_track_bt.setBackgroundColor(getResources().getColor(
                R.color.gray_background));
        LinearLayout pop_rescue_bt = (LinearLayout) contentView
                .findViewById(R.id.pop_rescue_bt);
        // pop_track_bt.setOnClickListener(this);
        pop_rescue_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // loRaManager.sosComplete(codeArr);
                sendCMDSOSComplete(codeArr);
                updataState(item.getNum());
                mPopWindow.dismiss();
                mPopWindow = null;
                showToast(getString(R.string.send_command_success_note));
            }
        });
        // 显示 PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(
                R.layout.security_fg, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

    }

    private void sendCMDSOSComplete(final byte[] num) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                loRaManager.sosComplete(num);
            }
        }).start();
    }

    private void updataState(String num) {
        List<PersonalInfo> list = CommonDBOperator.queryByKeys(persondao,
                "num", num);
        Log.e("Tag", "skladfjklasjf");
        if (list != null && list.size() > 0) {
            list.get(0).setState1(Constants.PERSON_COMMON);
            list.get(0).setTime(System.currentTimeMillis());
            CommonDBOperator.updateItem(persondao, list.get(0));
            initSoilderInfoList();
        }
    }

    private void deleteLocTrack() {
        if (pathId != -1) {
            mGraphicOverlay.DeleteFeature(pathId, DEFAULT_LAYER_NAME);
        }
    }

    //绘制普通曲线
    private void drawTrackLine(GeoPoint[] gPointArr) {
        if (pathId != -1) {
            mGraphicOverlay.DeleteFeature(pathId, DEFAULT_LAYER_NAME);
        }
        pathId = mGraphicOverlay.AddPolyline(gPointArr, Color.RED, DEFAULT_LAYER_NAME);
        refreshMap();
    }

    private void drawSyncFireLine(GeoPoint[] gPointArr) {
        if (syncFLId != -1) {
            mGraphicOverlay.DeleteFeature(pathId, DEFAULT_LAYER_NAME);
        }
        syncFLId = mGraphicOverlay.AddPolyline(gPointArr, Color.RED, DEFAULT_LAYER_NAME);
        refreshMap();
    }

    //根据坐标绘制火点
    private int drawFirePoint(GeoPoint geoPoint) {
        int newPointId = mGraphicOverlay.AddSituationPoint(geoPoint, 2001, 47, "火点", Color.RED, DEFAULT_LAYER_NAME);
        refreshMap();
        return newPointId;
    }

    //根据坐标绘制火线
    private int drawFireLine(GeoPoint[] geoPointArr, int data_type) {
        int newLineId = mGraphicOverlay.AddPolyline(geoPointArr, Color.RED, DEFAULT_LAYER_NAME);
        ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(DEFAULT_LAYER_NAME).GetFeatureByID(newLineId)).SetLineSymbolID(2001010047);
        ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(DEFAULT_LAYER_NAME).GetFeatureByID(newLineId)).SetLineWidth(3);
        ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(DEFAULT_LAYER_NAME).GetFeatureByID(newLineId)).SetLineColor(data_type == Constants.TX_FIRELINE ? Color.RED : Color.YELLOW);//设置火线颜色
//        refreshMap();
        return newLineId;
    }

    //标绘高亮显示
    private void setPlotHighLight(int id, String layerName) {
        Log.e("Tag", "id = " + id);
        int type = mGraphicOverlay.GetFeatureType(id, layerName);
        if (type == GM_TypeDefines.GM_FEATURE_TYPE_SITUATION_POINT) {
            //火点
            mGraphicOverlay.SetFeatureProp(id, PROP_NAME.Width, "" + 15, layerName);//设置火点大小
            mGraphicOverlay.SetFeatureProp(id, PROP_NAME.LineColor, "" + Color.GREEN, layerName);//设置火点颜色
        } else {
            //火线
            ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(layerName).GetFeatureByID(id)).SetLineSymbolID(id);
            ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(layerName).GetFeatureByID(id)).SetLineWidth(3);//设置火线线宽
            ((IGM_BaseLineFeature) mGraphicOverlay.GetLayerByName(layerName).GetFeatureByID(id)).SetLineColor(Color.GREEN);//设置火线颜色
        }
        refreshMap();
    }

    //刷新地图
    private void refreshMap() {
        if (mGraphicOverlay != null) {
            mGraphicOverlay.setModified();
            mMapView.postInvalidate();
        }
    }

    @Override
    public void onDismiss() {
        // popuwindow消失
        setTouched(-1);
        isShowing = false;
    }

    // 右侧标绘列表
    @Override
    public void onItemSelected(int pos) {
        Log.e("Tag", "onClick item pos : " + pos);
        if (firePlots.size() > 0) {
            deleteFirePFireL();
            firePlots.clear();
            initFirePFireL(pos);
        }
        setPlotHighLight(firePlots.get(pos).getMakerId(), DEFAULT_LAYER_NAME);
        GeoPoint geo = obtainArgPoint(pos);
        if (geo != null) {
            mapController = mMapView.getController();
            mapController.setCenter(geo);
        }
    }

    private GeoPoint obtainArgPoint(int pos) {
        GeoPoint geo = null;
        switch (firePlots.get(pos).getData_type()) {
            case Constants.TX_FIRELINE:
            case Constants.TX_SYNCFL:
                geo = BaseUtils.averagePoint(firePlots.get(pos).getPaths());
                break;
            case Constants.TX_FIREPOIT:
                String[] points = firePlots.get(pos).getPaths().split(",");
                geo = new GeoPoint(Double.valueOf(points[0]), Double.valueOf(points[1]));
                break;
        }
        return geo;
    }

    @Override
    public void initMessageNum() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int unReadMsgCount = 0;//初始化未读数字为0
                //1.先查出所有的未读消息
                List<MessageInfoIBean> list = CommonDBOperator.queryByMultiKeys(mMsgDao, mHashMap);
                if (list != null && list.size() > 0) {
                    //计算可通信的未读消息
                    for (int i = 0; i < list.size(); i++) {
                        //根据消息关联人的ID 查设备类型0 1
                        List<HttpPersonInfo> personInfos = CommonDBOperator.queryByKeys(mHttpDao, "id", String.valueOf(list.get(i).getID()));
                        if (personInfos != null && personInfos.size() > 0) {
                            if (personInfos.get(0).getDeviceType() == 0 || personInfos.get(0).getDeviceType() == 1) {//是可接受的消息
                                unReadMsgCount++;
                            }
                        }

                    }
                }
                if (unReadMsgCount == 0) {
                    if(getActivity()!=null){
                        getActivity().runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tv_conmnication_num.setText("");
                                tv_conmnication_num.setVisibility(View.GONE);
                            }
                        }));
                    }

                } else {
                    final int finalUnReadMsgCount = unReadMsgCount;
                    if(getActivity()!=null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_conmnication_num.setVisibility(View.VISIBLE);
                                if (finalUnReadMsgCount > 99) {
                                    tv_conmnication_num.setText("99+");
                                } else {
                                    tv_conmnication_num.setText(String.valueOf(finalUnReadMsgCount));
                                }
                            }
                        });
                        list.clear();
                    }
                }
               // mHashMap.clear();
            }
        }).start();

    }

    @Override
    protected void updateFeedback(int sendID) {
        List<TreeStateBean> list = CommonDBOperator.queryByKeys(treeDao, "SEND_ID", sendID + "");
        if (list != null && list.size() > 0) {
            list.get(0).setcState("2");
            CommonDBOperator.updateItem(treeDao, list.get(0));
            list.clear();
        }
        if (sendID == BaseUtils.getSendID()) {
            if (isOpen && isUpload) {
                tv_search_state.setText(getString(R.string.upload_success_1));
                tv_upload_state.setText(getString(R.string.upload_state_commplted));
                String[] rev1 = BaseUtils.formatTime(System.currentTimeMillis()).split("  ");
                tv_serach_date.setText(rev1[0]);
                tv_serach_time.setText(rev1[1]);
            }
        }
    }

    @Override
    protected void refleshBdSignal(Intent intent) {
        if (isOpen) {
            float[] values = intent.getFloatArrayExtra("values");
            his_bar.refleshView(values);
            switch (BDSignal.value) {
                case 1:
                    tv_signal_level.setText(getString(R.string.bd_signal_level_bad));
                    tv_signal_level.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                    break;
                case 2:
                    tv_signal_level.setText(getString(R.string.bd_signal_level_middle));
                    tv_signal_level.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                    break;
                case 3:
                    tv_signal_level.setText(getString(R.string.bd_signal_level_good));
                    tv_signal_level.setBackgroundColor(getActivity().getResources().getColor(R.color.green3));
                    break;
                case 4:
                    tv_signal_level.setText(getString(R.string.bd_signal_level_excellent));
                    tv_signal_level.setBackgroundColor(getActivity().getResources().getColor(R.color.green));
                    break;
            }
        }
    }

    private void showSearchDialog() {
        searchDialog = new ShowDialogSearch(getActivity());
        searchDialog.show();
        searchDialog.setListner(this);
    }

    /**
     * 上传dialog
     */

    public void showUploadDetailDialog(int total, int success, int fail, String body) {
        uploadDetail = new ShowUploadDetailDialog(getActivity(), total, success, fail, body);
        uploadDetail.show();
        uploadDetail.intContent();
        uploadDetail.setOnClickListener(this);
    }

    private void showHandleDialog() {
        handleDialog = new ShowHandlePlotDialog(getActivity());
        handleDialog.show();
        handleDialog.setListner(this);
    }

    private void setBDType(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loRaManager.changeWatchType(type);
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusBean eventBusBean) {
        if (isVisible && eventBusBean != null) {
            switch (eventBusBean.getCode()) {
                case Constants.EVENT_CODE_REFRESH_MSG_NUM://刷新未读消息数
                    LogUtil.d("eventBus有新的未读消息");
                    initMessageNum();
                    break;
            }
        }
    }

    @Override
    public void onScrollProgressChanged(float currentProgress) {
        if (currentProgress >= 0) {
            float precent = 255 * currentProgress;
            if (precent > 255) {
                precent = 255;
            } else if (precent < 0) {
                precent = 0;
            }
            mScrollLayout.getBackground().setAlpha(255 - (int) precent);
        }
        Log.e("Tag", "precent : " + currentProgress);
    }

    @Override
    public void onScrollFinished(ScrollLayout.Status currentStatus) {
        if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
//            mScrollLayout.getBackground().setAlpha(0);
            isOpen = false;
            if (mGraphicOverlay != null && mMapView != null) {
                mGraphicOverlay.SelectTool(-1, mMapView);
            }
            scroll_cancel.setVisibility(View.GONE);
            mFlContainer.setVisibility(View.VISIBLE);
        } else {
            mScrollLayout.setBackgroundColor(getResources().getColor(R.color.tra_gray));
            isOpen = true;
            if (mGraphicOverlay != null && mMapView != null) {
                mGraphicOverlay.SelectTool(GM_TypeDefines.GM_TOOL_EDIT_CREATE, mMapView); //禁止拖动  -1  可以拖动
            }
            mFlContainer.setVisibility(View.GONE);

        }
    }

    @Override
    public void onChildScroll(int top) {

    }

   // private PopupWindow mPopMemuWindow;
//    private void initPopWindow() {
//        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu,null);
//        //处理popWindow 显示内容
//        handleLogic(contentView);
//        mPopMemuWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT, true);
//        mPopMemuWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
//        mPopMemuWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
//        mPopMemuWindow.setContentView(contentView);
//        mPopMemuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mDragView.setVisibility(View.VISIBLE);
//            }
//        });
//
//
//
//    }
//    private void handleLogic(View contentView){
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // String showContent = "";
//                switch (v.getId()){
//                    case R.id.iv_btn_1:
//                       // showContent = "点击短消息菜单1";
//                        startActivity(new Intent(getActivity(), CommunicationListActivity.class));
//                        break;
//                    case R.id.iv_btn_2:
//                       // showContent = "点击 绘制菜单2";
//                        drawer.setScrimColor(Color.TRANSPARENT);
//                        drawer.openDrawer(Gravity.RIGHT);
//                        break;
//                    case R.id.iv_btn_3:
//                      //  showContent = "点击 搜索菜单3";
//                        if (isOpen) {
//                            mScrollLayout.scrollToExit();
//                        } else {
//                            mScrollLayout.setToOpen();
//                            scroll_cancel.setVisibility(View.VISIBLE);
//                        }
//                        break;
//                    case R.id.iv_btn_4:
//                      //  showContent = "点击 定位4";
//                        try {
//                            //将镜头平移到当前手持机定位的中心
//                            mapController = mMapView.getController();
//                            mapController.setCenter(new GeoPoint(lat, lon));
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
//                        break;
//                    case R.id.iv_btn_5:
//                     //   showContent = "点击服务菜单5" ;
//                        try {
//                            startActivity(new Intent("com.lhzw.intent.action_UPLOAD_SERVICE"));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//                if(mPopMemuWindow!=null){
//                    mPopMemuWindow.dismiss();
//                }
//
//            }
//
//        };
//        contentView.findViewById(R.id.iv_btn_1).setOnClickListener(listener);
//        contentView.findViewById(R.id.iv_btn_2).setOnClickListener(listener);
//        contentView.findViewById(R.id.iv_btn_3).setOnClickListener(listener);
//        contentView.findViewById(R.id.iv_btn_4).setOnClickListener(listener);
//        contentView.findViewById(R.id.iv_btn_5).setOnClickListener(listener);
//       // tv_conmnication_num =(TextView)contentView.findViewById(R.id.tv_conmnication_num);
//
//    }
}
