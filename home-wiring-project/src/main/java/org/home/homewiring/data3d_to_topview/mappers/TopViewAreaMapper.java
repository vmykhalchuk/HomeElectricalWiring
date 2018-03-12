package org.home.homewiring.data3d_to_topview.mappers;

import org.home.homewiring.data3dmodel.model.Area;
import org.home.homewiring.topview.model.TopViewArea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

// http://mapstruct.org/
@Mapper
public interface TopViewAreaMapper {

    @Mappings({
            @Mapping(target = "symbols", ignore = true)
    })
    TopViewArea areaToTopViewArea(Area area);
}
