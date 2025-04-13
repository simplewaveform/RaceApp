import { Autocomplete, Button, TextField, Dialog, DialogContent, DialogTitle } from '@mui/material';
import { useState } from 'react';
import axios from 'axios';

export default function RacesManager({ pilots, cars }) {
    const [raceData, setRaceData] = useState({
        name: '',
        year: '',
        pilotIds: [],
        carIds: []
    });

    const createRace = async () => {
        await axios.post('/api/races', raceData);
        // Обновление списка гонок
    };

    return (
        <Dialog open={true} maxWidth="md" fullWidth>
            <DialogTitle>Создать гонку</DialogTitle>
            <DialogContent sx={{ pt: 2 }}>
                <TextField
                    label="Название"
                    fullWidth
                    sx={{ mb: 2 }}
                    value={raceData.name}
                    onChange={e => setRaceData({...raceData, name: e.target.value})}
                />

                <Autocomplete
                    multiple
                    options={pilots}
                    getOptionLabel={p => p.name}
                    sx={{ mb: 2 }}
                    renderInput={params => (
                        <TextField {...params} label="Участники-пилоты" />
                    )}
                    onChange={(e, values) => setRaceData({
                        ...raceData,
                        pilotIds: values.map(v => v.id)
                    })}
                />

                <Autocomplete
                    multiple
                    options={cars}
                    getOptionLabel={c => `${c.brand} ${c.model}`}
                    renderInput={params => (
                        <TextField {...params} label="Участвующие автомобили" />
                    )}
                    onChange={(e, values) => setRaceData({
                        ...raceData,
                        carIds: values.map(v => v.id)
                    })}
                />

                <Button
                    variant="contained"
                    sx={{ mt: 2 }}
                    onClick={createRace}
                >
                    Сохранить
                </Button>
            </DialogContent>
        </Dialog>
    );
}