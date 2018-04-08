package com.example.listview;

/**
 * See builderpattern example project for how to do builders
 * they are essential when constructing complicated objects and
 * with many optional fields
 */
public class BikeData {
    public static final int COMPANY = 0;
    public static final int MODEL = 1;
    public static final int PRICE = 2;
    public static final int LOCATION = 3;
    final String Company;
    final String Model;
    final String Location;
    final String Price;
    final String Date;
    final String Description;
    final String Picture;
    final String Link;


    //TODO make all BikeData fields final

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO figure out how to print all bikedata out for dialogs
        String ender = "Company: " + this.Company + "\n" + "Model: " + this.Model + "\n" + "Location: " + this.Location + "\n" + "Date Listed: " + this.Date + "\n" + "Description: " + this.Description + "\n" + "Link: " + this.Link;
        return ender;
    }

    private BikeData(Builder b) {
        this.Company = b.Company;
        this.Model = b.Model;
        this.Location = b.Location;
        this.Price = b.Price.toString();
        this.Date = b.Date;
        this.Description = b.Description;
        this.Picture = b.Picture;
        this.Link = b.Link;
    }

    public BikeData(String Company, String Model, String Price, String Date, String Desc, String pic, String link, String Location) {
        this.Company = Company;
        this.Model = Model;
        this.Price = Price;
        this.Location = Location;
        this.Date = Date;
        this.Description = Desc;
        this.Picture = pic;
        this.Link = link;
    }

    /**
     * Return the param that the sort with use to sort the list
     *
     * @param s - the sort field that is chosen by the spinner
     * @return the part of the object that we want our list sorted on
     */
    public String getTag(String s) {
        String ender = "";
        switch (s) {
            case "Company":
                ender = this.Company;
                break;
            case "Location":
                ender = this.Location;
                break;
            case "Model":
                ender = this.Model;
                break;
            case "Price":
                ender = this.Price;
                break;
        }
        return ender;
    }

    /**
     * @author lynn builder pattern, see page 11 Effective Java UserData mydata
     *         = new
     *         UserData.Builder(first,last).addProject(proj1).addProject(proj2
     *         ).build()
     */
    public static class Builder {
        final String Company;
        final String Model;
        final Double Price;
        String Description;
        String Location;
        String Date;
        String Picture;
        String Link;

        // Model and price required
        Builder(String Company, String Model, Double Price) {
            this.Company = Company;
            this.Model = Model;
            this.Price = Price;
        }

        // the following are setters
        // notice it returns this bulder
        // makes it suitable for chaining
        Builder setDescription(String Description) {
            this.Description = Description;
            return this;
        }

        Builder setLocation(String Location) {
            this.Location = Location;
            return this;
        }

        Builder setDate(String Date) {
            this.Date = Date;
            return this;
        }

        Builder setPicture(String Picture) {
            this.Picture = Picture;
            return this;
        }

        Builder setLink(String Link) {
            this.Link = Link;
            return this;
        }

        // use this to actually construct Bikedata
        // without fear of partial construction
        public BikeData build() {
            return new BikeData(this);
        }
    }

}
