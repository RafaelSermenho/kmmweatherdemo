//
//  LocationViewModel.swift
//  KmmWeatherDemo_ios
//
//  Created by Rafael Sermenho on 04/08/21.
//

import Foundation
import CoreLocation
import kmmweathershared

class LocationViewModel: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var authorizationStatus: CLAuthorizationStatus
    @Published var lastSeenLocation: CLLocation?
    @Published var currentPlacemark: CLPlacemark?
    @Published var weatherForecast: String = ""
    
    
    private let locationManager: CLLocationManager
    
    override init() {
        locationManager = CLLocationManager()
        authorizationStatus = locationManager.authorizationStatus
        
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }
    
    func requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        authorizationStatus = manager.authorizationStatus
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        lastSeenLocation = locations.first
        fetchCountryAndCity(for: locations.first)
    }
    
    func fetchCountryAndCity(for location: CLLocation?) {
        guard let location = location else { return }
        let geocoder = CLGeocoder()
        geocoder.reverseGeocodeLocation(location) { (placemarks, error) in
            self.currentPlacemark = placemarks?.first
            if (self.currentPlacemark != nil && self.currentPlacemark?.locality != nil) {
                OpenWeatherAPI().getWeatherForCity(city: (self.currentPlacemark?.locality!)!) { response, _ in
                    guard
                        let data = response
                    
                    else {
                        return
                    }
                    
                    do {
                        let desc = data.weather[0].description_
                        let temp = data.main.temp
                        self.weatherForecast = desc + " " + String(format: "%.1f", temp)
                    }
                }
            }
        }
    }
}
