package acs.dal;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import acs.Location;
import acs.data.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, Long>  {
	public List<ElementEntity> findAllByParentElement_elementId(@Param("parentElementId") Long parentElementId, Pageable pageable);
	
	public List<ElementEntity> findAllByName(@Param("name") String elementName, Pageable pageable);
	
	public List<ElementEntity> findAllByType(@Param("type") String elementType, Pageable pageable);

	public List<ElementEntity> getAllByTypeAndNameAndLocation_lngBetweenAndLocation_latBetween(@Param("type") String elementType,
			@Param("name") String elementName,
			@Param("lngMin")Double minLng,@Param("lngMax")Double maxLng,
			@Param("latMin")Double minLat,@Param("latMax")Double maxLat,
			Pageable pageable);
	
}
