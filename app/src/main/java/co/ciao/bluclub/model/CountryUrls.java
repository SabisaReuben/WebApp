package co.ciao.bluclub.model;

public enum CountryUrls {
    EGYPT("https://www.truehost.com"),
    KENYA("https://www.truehost.co.ke"),
    S_AFRICA("https://www.truehost.com"),
    NIGERIA("https://www.truehost.com"),
    REST_OF_WORLD("https://www.truehost.com");
    private String url;
    CountryUrls(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
