package io.mountainsea.kun.util.marketplace;

import lombok.Data;

import java.util.List;

/**
 * @author : ShaoHongLiang
 * @date : 2022/11/3
 */
@Data
public class AppleSoftwareInfo {

    private List<String> screenshotUrls;

    private Boolean isGameCenterEnabled;

    private List<String> features;

    private String artworkUrl100;

    private String artistViewUrl;

    private List<String> supportedDevices;

    private List<String> advisories;

    private String ipadScreenshotUrls;

    private String artworkUrl60;

    private String artworkUrl512;

    private List<String> appletvScreenshotUrls;

    private String kind;

    private String currentVersionReleaseDate;

    private String releaseNotes;

    private String minimumOsVersion;

    private String trackCensoredName;

    private List<String> languageCodesISO2A;

    private Long fileSizeBytes;

    private String sellerUrl;

    private String formattedPrice;

    private String contentAdvisoryRating;

    private Double averageUserRatingForCurrentVersion;

    private Long userRatingCountForCurrentVersion;

    private Double averageUserRating;

    private String trackViewUrl;

    private String trackContentRating;

    private Long artistId;

    private String artistName;

    private List<String> genres;

    private Double price;

    private String sellerName;

    private String description;

    private String bundleId;

    private Long trackId;

    private String trackName;

    private List<String> genreIds;

    private Boolean isVppDeviceBasedLicensingEnabled;

    private String primaryGenreName;

    private Long primaryGenreId;

    private String releaseDate;

    private String currency;

    private String version;

    private String wrapperType;

    private Long userRatingCount;

    private String downloadUrl;
}
