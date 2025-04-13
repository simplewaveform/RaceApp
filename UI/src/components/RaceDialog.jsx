import { useState, useEffect } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, Autocomplete, Alert, CircularProgress
} from '@mui/material';
import axios from 'axios';

export default function RaceDialog({
                                       open,
                                       onClose,
                                       relations = { pilots: [], cars: [] },
                                       onError,
                                       onSuccess,
                                       raceToEdit
                                   }) {
    const [form, setForm] = useState({
        name: '',
        year: '',
        pilotIds: [],
        carIds: []
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [localRelations, setLocalRelations] = useState(relations);
    const isEditMode = !!raceToEdit;

    useEffect(() => {
        setLocalRelations(relations);
    }, [relations, open]);

    useEffect(() => {
        if (raceToEdit) {
            setForm({
                name: raceToEdit.name || '',
                year: raceToEdit.year || '',
                pilotIds: raceToEdit.pilots?.map(p => p?.id)?.filter(Boolean) || [],
                carIds: raceToEdit.cars?.map(c => c?.id)?.filter(Boolean) || []
            });
        } else {
            setForm({ name: '', year: '', pilotIds: [], carIds: [] });
        }
    }, [raceToEdit]);

    const validate = () => {
        const newErrors = {};
        if (!form.name.trim()) newErrors.name = 'Введите название';
        if (form.year < 1900) newErrors.year = 'Год должен быть после 1900';
        if (form.pilotIds.length === 0) newErrors.pilots = 'Выберите пилотов';
        if (form.carIds.length === 0) newErrors.cars = 'Выберите автомобили';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        setLoading(true);
        try {
            const url = isEditMode ? `/api/races/${raceToEdit.id}` : '/api/races';
            const method = isEditMode ? 'put' : 'post';

            await axios[method](url, form);

            onSuccess(isEditMode ? 'Гонка успешно обновлена' : 'Гонка успешно создана');
            onClose();
        } catch (error) {
            onError(error.response?.data?.message || 'Ошибка операции');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>
                {isEditMode ? 'Редактировать гонку' : 'Создать новую гонку'}
            </DialogTitle>

            <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
                {Object.values(errors).map((error, i) => (
                    <Alert key={i} severity="error">{error}</Alert>
                ))}

                <TextField
                    label="Название"
                    fullWidth
                    error={!!errors.name}
                    value={form.name}
                    onChange={e => setForm({...form, name: e.target.value})}
                />

                <TextField
                    label="Год"
                    type="number"
                    fullWidth
                    error={!!errors.year}
                    value={form.year}
                    onChange={e => setForm({...form, year: e.target.value})}
                    inputProps={{ min: 1900 }}
                />

                <Autocomplete
                    multiple
                    options={localRelations.pilots}
                    getOptionLabel={p => p?.name || 'Неизвестный пилот'}
                    value={localRelations.pilots.filter(p => form.pilotIds.includes(p?.id))}
                    onChange={(e, values) => setForm({
                        ...form,
                        pilotIds: values.map(v => v?.id).filter(Boolean)
                    })}
                    renderInput={params => (
                        <TextField
                            {...params}
                            label="Участники"
                            error={!!errors.pilots}
                        />
                    )}
                />

                <Autocomplete
                    multiple
                    options={localRelations.cars}
                    getOptionLabel={c => `${c?.brand || ''} ${c?.model || ''}`.trim() || 'Неизвестный автомобиль'}
                    value={localRelations.cars.filter(c => form.carIds.includes(c?.id))}
                    onChange={(e, values) => setForm({
                        ...form,
                        carIds: values.map(v => v?.id).filter(Boolean)
                    })}
                    renderInput={params => (
                        <TextField
                            {...params}
                            label="Автомобили"
                            error={!!errors.cars}
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
                    ) : isEditMode ? 'Обновить' : 'Создать'}
                </Button>
            </DialogActions>
        </Dialog>
    );
}