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
                .filter(route -> route.getStationIds().containsAll(List.of(startStationId, endStationId)))
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
        return myTrainVO;
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
