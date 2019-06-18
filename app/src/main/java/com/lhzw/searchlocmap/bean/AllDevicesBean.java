package com.lhzw.searchlocmap.bean;

import java.util.List;

/**
 * Created by hecuncun on 2019/6/17
 */
public class AllDevicesBean  {

    /**
     * content : [{"deviceNumber":"0337031","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0402052","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0402050","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"033700916","deviceName":"手持机3","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0244947","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0319330","deviceName":"手持机330","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0319332","deviceName":"手持机332","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0402053","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0402051","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"03370091","deviceName":"手持机3","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0402064","deviceName":"","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0402058","deviceName":null,"deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":true},{"deviceNumber":"0337009","deviceName":"手持机3","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0337001","deviceName":"手持机","deviceType":"1","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0000000002","deviceName":"手表3","deviceType":"2","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"10000000001","deviceName":"手表4","deviceType":"2","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"00000121","deviceName":"手表2","deviceType":"2","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0000022345","deviceName":"手表3","deviceType":"2","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"00002000012","deviceName":"手表2","deviceType":"2","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false},{"deviceNumber":"0000200002","deviceName":"手表1","deviceType":"2","lng":null,"lat":null,"positionTime":null,"createTime":null,"deleted":false,"bound":false}]
     * pageable : {"sort":{"sorted":false,"unsorted":true},"offset":0,"pageSize":20,"pageNumber":0,"paged":true,"unpaged":false}
     * last : false
     * totalPages : 6
     * totalElements : 107
     * number : 0
     * size : 20
     * sort : {"sorted":false,"unsorted":true}
     * numberOfElements : 20
     * first : true
     */

    private PageableBean pageable;
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int number;
    private int size;
    private SortBeanX sort;
    private int numberOfElements;
    private boolean first;
    private List<ContentBean> content;

    public PageableBean getPageable() {
        return pageable;
    }

    public void setPageable(PageableBean pageable) {
        this.pageable = pageable;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public SortBeanX getSort() {
        return sort;
    }

    public void setSort(SortBeanX sort) {
        this.sort = sort;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class PageableBean {
        /**
         * sort : {"sorted":false,"unsorted":true}
         * offset : 0
         * pageSize : 20
         * pageNumber : 0
         * paged : true
         * unpaged : false
         */

        private SortBean sort;
        private int offset;
        private int pageSize;
        private int pageNumber;
        private boolean paged;
        private boolean unpaged;

        public SortBean getSort() {
            return sort;
        }

        public void setSort(SortBean sort) {
            this.sort = sort;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public boolean isPaged() {
            return paged;
        }

        public void setPaged(boolean paged) {
            this.paged = paged;
        }

        public boolean isUnpaged() {
            return unpaged;
        }

        public void setUnpaged(boolean unpaged) {
            this.unpaged = unpaged;
        }

        public static class SortBean {
            /**
             * sorted : false
             * unsorted : true
             */

            private boolean sorted;
            private boolean unsorted;

            public boolean isSorted() {
                return sorted;
            }

            public void setSorted(boolean sorted) {
                this.sorted = sorted;
            }

            public boolean isUnsorted() {
                return unsorted;
            }

            public void setUnsorted(boolean unsorted) {
                this.unsorted = unsorted;
            }
        }
    }

    public static class SortBeanX {
        /**
         * sorted : false
         * unsorted : true
         */

        private boolean sorted;
        private boolean unsorted;

        public boolean isSorted() {
            return sorted;
        }

        public void setSorted(boolean sorted) {
            this.sorted = sorted;
        }

        public boolean isUnsorted() {
            return unsorted;
        }

        public void setUnsorted(boolean unsorted) {
            this.unsorted = unsorted;
        }
    }

    public static class ContentBean {
        /**
         * deviceNumber : 0337031
         * deviceName : null
         * deviceType : 1
         * lng : null
         * lat : null
         * positionTime : null
         * createTime : null
         * deleted : false
         * bound : false
         */

        private String deviceNumber;
        private String deviceName;
        private int deviceType;
        private String lng;
        private String lat;
        private String positionTime;
        private String createTime;
        private boolean deleted;
        private boolean bound;

        public String getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public Object getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public Object getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public Object getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public Object getPositionTime() {
            return positionTime;
        }

        public void setPositionTime(String positionTime) {
            this.positionTime = positionTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public boolean isBound() {
            return bound;
        }

        public void setBound(boolean bound) {
            this.bound = bound;
        }
    }
}
