package ch.epfl.sweng.rps.utils

class StateNotifier<T>(initialValue: T) : ChangeNotifier() {
    private var _state: T = initialValue

    var value: T
        get() = _state
        set(value) {
            if (value != _state) {
                _state = value
                notifyListeners()
            }
        }
}