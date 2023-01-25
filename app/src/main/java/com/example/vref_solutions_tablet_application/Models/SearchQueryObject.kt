package com.example.vref_solutions_tablet_application.Models

class SearchQueryObject {
    val query: String
    var amountOfUsage: Int


    constructor(query: String, amountOfUsage: Int) {
        this.query = query
        this.amountOfUsage = amountOfUsage
    }
}