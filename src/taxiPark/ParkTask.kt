package taxiPark

/*
 * Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        allPassengers
        .filter { p->
                trips.count { p in it.passengers } >= minTrips
        }
        .toSet()

/*
 * Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> = allDrivers - trips.map { it.driver }

/*
 * Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        allPassengers
                .filter { p ->
                    trips.count { it.driver == driver && p in it.passengers } >1
        }
        .toSet()

/*
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (this.trips.isEmpty()) {
        return false
    } else {
        val tripsCost = this.trips.map(Trip::cost).sum()
        val driverCost = trips
            .groupBy(Trip::driver)
            .mapValues { (_, trip) -> trip.sumByDouble(Trip::cost) }
            .toList()
            .sortedByDescending { (_, value) -> value }.toMap()

        var sum = 0.0
        var drivers = 0
        for (value in driverCost.values) {
            drivers++
            sum += value
            if (sum >= (tripsCost * 0.8)) break
        }
        return drivers <= (allDrivers.size * 0.2)
    }
}

/*
 * Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> =
        allPassengers.associateWith { p -> trips.filter { t -> p in t.passengers } }
        .filterValues { group ->
            val (withDiscount, withoutDiscount) = group
                .partition { it.discount != null }
                withDiscount.size > withoutDiscount.size
        }
            .keys

/*
 * Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    return trips
        .groupBy {
            val start = it.duration / 10 * 10
            val end = start + 9
            start..end
        }
        .maxBy { (_, group) -> group.size }
        ?.key
}

