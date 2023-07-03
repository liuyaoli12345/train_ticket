package org.fffd.l23o6.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;
//<<<<<<< src/main/java/org/fffd/l23o6/service/impl/TrainServiceImpl.java
//=======
import org.fffd.l23o6.util.strategy.paymentUtil;
//>>>>>>> src/main/java/org/fffd/l23o6/service/impl/TrainServiceImpl.java
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService
{
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainDetailVO getTrain(Long trainId)
    {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfos()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date)
    {
        // TODO 2023-06-30 by 刘尧力
        // First, get all routes contains [startCity, endCity]
        // Then, Get all trains on that day with the wanted routes
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        List<RouteEntity> routes = routeDao.findAll(Sort.by(Sort.Direction.ASC, "name"));
        List<Long> filteredRouteIds = routes.stream()
                .filter(route -> {
                    List<Long> stationIds = route.getStationIds();
                    int startIndex = stationIds.indexOf(startStationId);
                    int endIndex = stationIds.indexOf(endStationId);
                    return startIndex != -1 && endIndex != -1 && startIndex < endIndex;
                })
                .map(RouteEntity::getId).toList();
        List<TrainEntity> filteredTrains = trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .filter(train -> train.getDate().equals(date) && filteredRouteIds.contains(train.getRouteId())).toList();
        return filteredTrains.stream()
                .map(this::toTrainVO).collect(Collectors.toList());
    }

    private Long startStationId;
    private Long endStationId;

    private TrainVO toTrainVO(TrainEntity TrainEntity)
    {
        TrainVO myTrainVO = TrainMapper.INSTANCE.toTrainVO(TrainEntity);
        //myTrainVO.setTrainType();
        myTrainVO.setStartStationId(startStationId);
        myTrainVO.setEndStationId(endStationId);
        Long routeID = TrainEntity.getRouteId();
        RouteEntity routeEntity = routeDao.findById(routeID).get();
        int start = routeEntity.getStationIds().indexOf(startStationId);
        int end = routeEntity.getStationIds().indexOf(endStationId);
        myTrainVO.setDepartureTime(TrainEntity.getDepartureTimes().get(start));
        myTrainVO.setArrivalTime(TrainEntity.getArrivalTimes().get(end));
        int type = TrainEntity.getTrainType().getText().equals("高铁") ? 1 : 2;
        myTrainVO.setTicketInfo(genTicketInfoList(TrainEntity.getSeats(),type,start, end));
       //myTrainVO.setTicketInfo(TrainEntity.get);
        //int type = TrainEntity.getTrainType().getText().equals("高铁") ? 1 : 2;
       //myTrainVO.setTicketInfo(genTicketInfoList(TrainEntity.getSeats(),type,start, end));
        return myTrainVO;
    }

    // type1 = G, type2 = K
    // 两种列车都采用了硬编码，高铁是3,12,15,普快是8,12,16,20
    private boolean check(boolean[][] seats, int startStation, int endStation, int tar){
        for (int i=startStation; i<endStation; i++){
            if (!seats[i][tar])
                return false;
        }
        return true;
    }

    private TicketInfo genTicketInfo(boolean[][] seats, String type, int startStation, int endStation, TrainSeatStrategy.SeatType seatType, int cmd, int seatTypeInt){
        TicketInfo info = new TicketInfo();
        info.setType(type);
        int count=0;
//        for (int i=start; i<end; i++){
//            if (check(seats,startStation,endStation,i))
//                count++;
//        }
        if (cmd == 1)
            info.setCount((GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startStation,endStation,seats).get(seatType)));
        else
            info.setCount((KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startStation,endStation,seats).get(seatType)));
        //TODO:采用特定的价格策略生成车票的价格 2023-7-1 by 刘尧力
        info.setPrice(paymentUtil.genPrice(cmd, endStation-startStation, seatTypeInt));
        return info;
    }

    private List<TicketInfo> genTicketInfoList(boolean[][] seats, int type, int startStation, int endStation){
        List<TicketInfo> infos = new ArrayList<>();
        if (type == 1){
            infos.add(genTicketInfo(seats,"商务座",startStation,endStation, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT, 1,0));
            infos.add(genTicketInfo(seats,"一等座",startStation, endStation, GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT, 1,1));
            infos.add(genTicketInfo(seats,"二等座", startStation, endStation, GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT, 1,2));
            infos.add(genTicketInfo(seats, "无座", startStation,endStation, GSeriesSeatStrategy.GSeriesSeatType.NO_SEAT, 1,3));
        } else if (type==2){
            infos.add(genTicketInfo(seats, "软卧", startStation,endStation, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT, 2,0));
            infos.add(genTicketInfo(seats, "硬卧", startStation,endStation, KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT, 2,1));
            infos.add(genTicketInfo(seats, "软座", startStation,endStation, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT, 2,2));
            infos.add(genTicketInfo(seats, "硬座", startStation,endStation, KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT, 2,3));
            infos.add(genTicketInfo(seats, "无座", startStation,endStation, KSeriesSeatStrategy.KSeriesSeatType.NO_SEAT, 2,4));
        }
        return infos;
    }



    @Override
    public List<AdminTrainVO> listTrainsAdmin()
    {
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                         List<Date> departureTimes)
    {
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size())
        {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        switch (entity.getTrainType())
        {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                            List<Date> departureTimes)
    {
        // TODO: edit train info, please refer to `addTrain` above 2023-6-30 by 刘尧力
        //select the train by id and delete 这个方法如果更改消息出错，会把原来火车信息删除，可能有点问题
        trainDao.delete(trainDao.getReferenceById(id));

        addTrain(name, routeId, type, date, arrivalTimes, departureTimes);
    }

    @Override
    public void deleteTrain(Long id)
    {
        trainDao.deleteById(id);
    }



}
