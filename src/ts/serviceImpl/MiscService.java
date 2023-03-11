package ts.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import ts.daoImpl.RegionDao;
import ts.model.CodeNamePair;
import ts.model.Region;
import ts.model.RegionParam;
import ts.service.IMiscService;

public class MiscService implements IMiscService{
	

	private RegionDao regionDao;


	public RegionDao getRegionDao() {
		return regionDao;
	}

	public void setRegionDao(RegionDao dao) {
		this.regionDao = dao;
	}

	public MiscService(){
	}
//实现部分=======================================================================================================================

	//demo保留======================================================================================================================
	@Override
	public List<CodeNamePair> getProvinceList() {		
		List<Region> listrg = regionDao.getProvinceList();
		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
		for(Region rg : listrg){
			CodeNamePair cn = new CodeNamePair(rg.getORMID(),rg.getPrv());
			listCN.add(cn);
		}
		return listCN;
	}

	@Override
	public List<CodeNamePair> getCityList(String prv) {
		List<Region> listrg = regionDao.getCityList(prv);
		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
		for(Region rg : listrg){
			CodeNamePair cn = new CodeNamePair(rg.getORMID(),rg.getCty());
			listCN.add(cn);
		}
		return listCN;
	}

	@Override
	public List<CodeNamePair> getTownList(String city) {
		List<Region> listrg = regionDao.getTownList(city);
		List<CodeNamePair> listCN = new ArrayList<CodeNamePair>();
		for(Region rg : listrg){
			CodeNamePair cn = new CodeNamePair(rg.getORMID(),rg.getTwn());
			listCN.add(cn);
		}
		return listCN;
	}

	@Override
	public String getRegionString(String code) {
		return regionDao.getRegionNameByID(code);
	}

	@Override
	public Region getRegion(String code) {
		return regionDao.getFullNameRegionByID(code);
	}
	
	/*
     * 添加行政区域
     */
	@Override
	public Response addRegionInfo(int stage, String code, String name) {
		List<Region> reList = regionDao.findBy("regionCode", code, "regionCode", true);
		if(reList.size()==0) {
			Region region = new Region();
			if(stage==2) {
				region.setCty(name);
			}else if(stage==3) {
				region.setTwn(name);
			}
			region.setRegionCode(code);
			region.setStage(stage);
			try {
				regionDao.save(region);
				return Response.ok(region).header("EntityClass", "R_ExpressSheet").build();
			} catch (Exception e) {
				return Response.serverError().entity(e.getMessage()).build();
			}
		}else {
			return Response.ok("111").header("EntityClass", "R_ExpressSheet").build();
		}
	}

	/*
     * 获取行政地域所有节点信息-生成ztree数据
     */
	@Override
	public List<RegionParam> getRegionZtree() {
		List<Region> rList =regionDao.getAll();
		List<RegionParam> rpList = new ArrayList<RegionParam>();
		for (Region region : rList) {
			if(region.getStage()==1) {
				RegionParam rp = new RegionParam();
				rp.setName(region.getPrv());
				rp.setpId("0");
				rp.setId(region.getRegionCode());
				rpList.add(rp);
			}else if(region.getStage()==2) {
				RegionParam rp = new RegionParam();
				rp.setName(region.getCty());
				rp.setpId(region.getRegionCode().substring(0, 2)+"0000");
				rp.setId(region.getRegionCode());
				rpList.add(rp);
			}else if(region.getStage()==3) {
				RegionParam rp = new RegionParam();
				rp.setName(region.getTwn());
				rp.setpId(region.getRegionCode().substring(0, 4)+"00");
				rp.setId(region.getRegionCode());
				rpList.add(rp);
			}
		}
		return rpList;
	}
	
}
