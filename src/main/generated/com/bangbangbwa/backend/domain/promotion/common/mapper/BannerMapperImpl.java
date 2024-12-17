package com.bangbangbwa.backend.domain.promotion.common.mapper;

import com.bangbangbwa.backend.domain.promotion.common.dto.PromotionBannerDto;
import com.bangbangbwa.backend.domain.promotion.common.entity.Banner;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-15T20:27:39+0900",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
public class BannerMapperImpl implements BannerMapper {

    @Override
    public List<PromotionBannerDto.PromotionBanner> entityToDto(List<Banner> banners) {
        if ( banners == null ) {
            return null;
        }

        List<PromotionBannerDto.PromotionBanner> list = new ArrayList<PromotionBannerDto.PromotionBanner>( banners.size() );
        for ( Banner banner : banners ) {
            list.add( bannerToPromotionBanner( banner ) );
        }

        return list;
    }

    protected PromotionBannerDto.PromotionBanner bannerToPromotionBanner(Banner banner) {
        if ( banner == null ) {
            return null;
        }

        String url = null;
        String bgColor = null;

        url = banner.getUrl();
        bgColor = banner.getBgColor();

        PromotionBannerDto.PromotionBanner promotionBanner = new PromotionBannerDto.PromotionBanner( url, bgColor );

        return promotionBanner;
    }
}
