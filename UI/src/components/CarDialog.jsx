import { useState, useEffect } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, Autocomplete, Alert, CircularProgress
} from '@mui/material';
import axios from 'axios';

export default function CarDialog({
                                      open,
                                      onClose,
                                      relations = { pilots: [], cars: [] },
                                      onError,
                                      onSuccess,
                                      carToEdit,
                                      fetchCars
                                  }) {
    const [form, setForm] = useState({
        brand: '',
        model: '',
        power: '',
        ownerId: ''
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const isEditMode = !!carToEdit;

    useEffect(() => {
        if (carToEdit) {
            setForm({
                brand: carToEdit.brand || '',
                model: carToEdit.model || '',
                power: carToEdit.power || '',
                ownerId: carToEdit.owner?.id || ''
            });
        } else {
            setForm({ brand: '', model: '', power: '', ownerId: '' });
        }
    }, [carToEdit]);

    const validate = () => {
        const newErrors = {};
        if (!form.brand.trim()) newErrors.brand = 'Введите марку';
        if (!form.model.trim()) newErrors.model = 'Введите модель';
        if (form.power <= 0) newErrors.power = 'Мощность должна быть положительной';
        if (!form.ownerId) newErrors.ownerId = 'Выберите владельца';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        setLoading(true);
        try {
            const url = isEditMode
                ? `/api/cars/${carToEdit.id}`
                : '/api/cars';

            const method = isEditMode ? 'put' : 'post';

            await axios[method](url, form);

            onSuccess(isEditMode
                ? 'Автомобиль успешно обновлен'
                : 'Автомобиль успешно создан');
            onClose();
        } catch (error) {
            onError(error.response?.data?.message || 'Ошибка операции');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>
                {isEditMode ? 'Редактировать автомобиль' : 'Добавить автомобиль'}
            </DialogTitle>

            <DialogContent sx={{ pt: 2 }}>
                {Object.values(errors).map((error, i) => (
                    <Alert key={i} severity="error" sx={{ mb: 2 }}>{error}</Alert>
                ))}

                <TextField
                    label="Марка"
                    fullWidth
                    sx={{ mb: 2 }}
                    error={!!errors.brand}
                    value={form.brand}
                    onChange={e => setForm({...form, brand: e.target.value})}
                />

                <TextField
                    label="Модель"
                    fullWidth
                    sx={{ mb: 2 }}
                    error={!!errors.model}
                    value={form.model}
                    onChange={e => setForm({...form, model: e.target.value})}
                />

                <TextField
                    label="Мощность (л.с.)"
                    type="number"
                    fullWidth
                    sx={{ mb: 2 }}
                    error={!!errors.power}
                    value={form.power}
                    onChange={e => setForm({...form, power: e.target.value})}
                    inputProps={{ min: 1 }}
                />

                <Autocomplete
                    options={relations.pilots}
                    getOptionLabel={(pilot) => pilot?.name || 'Без имени'}
                    value={relations.pilots.find(p => p.id === form.ownerId) || null}
                    onChange={(e, value) => {
                        setForm({ ...form, ownerId: value?.id || null });
                    }}
                    isOptionEqualToValue={(option, value) => option?.id === value?.id}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            label="Владелец"
                            error={!!errors.ownerId}
                        />
                    )}
                />
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} disabled={loading}>Отмена</Button>
                <Button
                    onClick={handleSubmit}
                    variant="contained"
                    disabled={loading}
                >
                    {loading ? (
                        <CircularProgress size={24} />
                    ) : isEditMode ? 'Обновить' : 'Сохранить'}
                </Button>
            </DialogActions>
        </Dialog>
    );
}